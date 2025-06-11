package dev.openrune.wiki.dumpers

import com.google.gson.GsonBuilder
import dev.openrune.cache.CacheManager
import dev.openrune.server.ServerCacheManager
import dev.openrune.wiki.DATA_LOCATION
import dev.openrune.wiki.EncodingSettings
import dev.openrune.wiki.WikiDumper
import dev.openrune.wiki.dumpers.impl.Items as ItemParser
import dev.openrune.wiki.dumpers.impl.WorldItemSpawns
import io.github.oshai.kotlinlogging.KotlinLogging
import me.tongfei.progressbar.ProgressBarBuilder
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.createDirectories

object Items {

    private val logger = KotlinLogging.logger {}
    private val gson = GsonBuilder().setPrettyPrinting().create()
    var ITEM_LOCATION = DATA_LOCATION.resolve("items")

    fun init() {
        ITEM_LOCATION = DATA_LOCATION.resolve("items")
        ITEM_LOCATION.createDirectories()
        val encodingSettings = EncodingSettings(linkedIds = false)

        parseWorldItemSpawns(encodingSettings)
        parseItems(encodingSettings)
        downloadExamineCsv()
        //dumpItemIcons()
    }

    private fun parseWorldItemSpawns(settings: EncodingSettings) {
        val worldItemSpawns = WorldItemSpawns()
        logger.info { "Parsing World Item Spawns..." }
        worldItemSpawns.parseItem()

        // Write the complete spawns JSON
        ITEM_LOCATION.resolve("item-spawns-complete.json").toFile()
            .writeText(gson.toJson(worldItemSpawns.toWrite(settings)))

        // Write individual spawn files per item
        val spawnsDir = ITEM_LOCATION.resolve("world-spawns-json").apply { createDirectories() }
        worldItemSpawns.itemSpawns.forEach { (id, spawns) ->
            val name = "$id"
            spawnsDir.resolve("$name.json").toFile().writeText(gson.toJson(spawns))
        }
    }

    private fun parseItems(settings: EncodingSettings) {
        val items = ItemParser()
        logger.info { "Parsing Items..." }
        items.parseItem()

        ITEM_LOCATION.resolve("items-wiki-only.json").toFile()
            .writeText(gson.toJson(items.toWrite(settings)))

        ITEM_LOCATION.resolve("items-cache-only.json").toFile()
            .writeText(gson.toJson(CacheManager.getItems()))
    }

    fun writeServerData() {
        val completeItems = ServerCacheManager.getItems()
        val jsonDir = ITEM_LOCATION.resolve("items-server-json").apply { createDirectories() }

        ITEM_LOCATION.resolve("items-complete.json").toFile()
            .writeText(gson.toJson(completeItems))

        completeItems.forEach { (id, itemDef) ->
            jsonDir.resolve("${id}.json").toFile().writeText(gson.toJson(itemDef))
        }
    }

    private fun downloadExamineCsv() {
        val url = URL("https://raw.githubusercontent.com/Joshua-F/osrs-examines/refs/heads/master/objs.csv")
        val targetPath = ITEM_LOCATION.resolve("item-examines.csv")

        logger.info { "Downloading objs.csv from GitHub..." }
        url.openStream().use { input ->
            Files.copy(input, targetPath, StandardCopyOption.REPLACE_EXISTING)
        }
        logger.info { "objs.csv downloaded to $targetPath" }
    }

    private fun dumpItemIcons() {
        val pages = WikiDumper.wiki.pages.asSequence()
            .filter { it.namespace.key == 0 && it.revision.text.contains("infobox item", ignoreCase = true) }

        val progressBar = ProgressBarBuilder()
            .setInitialMax(pages.count().toLong())
            .setTaskName("Dumping Item Images")
            .build()

        val iconsDir = ITEM_LOCATION.resolve("icons").apply { createDirectories() }

        pages.forEach { page ->
            val template = page.getTemplateMaps("infobox item").firstOrNull() ?: return@forEach

            val idKeys = template.keys.filter { it == "id" || it.matches(Regex("id\\d+")) }
            val ids = mutableListOf<Pair<Int, Int>>()

            idKeys.indices.forEach { i ->
                extractIds(template, i + 1, ids)
            }

            ids.forEach { (id, index) ->
                val imageName = extractValueField("image", template, index).orEmpty().replace("<br>","").replace("<br/>","")
                if (imageName.isEmpty()) return@forEach

                imageName.split(".png]]").forEachIndexed { imgIdx, name ->
                    val cleanName = name.replace("[[File:", "").trimStart()

                    val urlName = cleanName.replace(" ", "_")
                    if (urlName.isNotEmpty()) {
                        val imageUrl = "https://oldschool.runescape.wiki/images/$urlName.png"

                        val stackVariants = CacheManager.getItem(id)?.countObj
                        val targetId = stackVariants?.getOrNull(imgIdx) ?: id
                        downloadImage(imageUrl, iconsDir.resolve("$targetId.png"))
                    }
                }
            }

            progressBar.step()
        }

        progressBar.close()
    }

    private fun downloadImage(url: String, path: Path) {
        try {
            Files.createDirectories(path.parent)
            if (Files.exists(path)) return

            val uri = URI(
                "https", "oldschool.runescape.wiki",
                "/images/" + URL(url).path.substringAfterLast("/"),
                null
            )
            val encodedUrl = uri.toURL()

            encodedUrl.openStream().use { input ->
                Files.newOutputStream(path).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            //e.printStackTrace()
            //logger.warn { "Failed to download image to $path: ${e.message}" }
        }
    }

}
