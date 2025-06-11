package dev.openrune.wiki.dumpers

import com.google.gson.GsonBuilder
import dev.openrune.cache.CacheManager
import dev.openrune.server.ServerCacheManager
import dev.openrune.wiki.DATA_LOCATION
import dev.openrune.wiki.EncodingSettings
import dev.openrune.wiki.dumpers.impl.Npcs;
import io.github.oshai.kotlinlogging.KotlinLogging
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.createDirectories

object Npcs {

    private val logger = KotlinLogging.logger {}
    private val gson = GsonBuilder().setPrettyPrinting().create()
    var NPC_LOCATION = DATA_LOCATION.resolve("npcs")

    fun init() {
        NPC_LOCATION = DATA_LOCATION.resolve("npcs")

        NPC_LOCATION.createDirectories()
        val encodingSettings = EncodingSettings(linkedIds = false)

        parseObjects(encodingSettings)
        downloadExamineCsv()
    }

    private fun parseObjects(settings: EncodingSettings) {
        val npcs = Npcs()
        logger.info { "Parsing Npcs..." }
        npcs.parseItem()

        NPC_LOCATION.resolve("npcs-wiki-only.json").toFile()
            .writeText(gson.toJson(npcs.toWrite(settings)))

        NPC_LOCATION.resolve("npcs-cache-only.json").toFile()
            .writeText(gson.toJson(CacheManager.getObjects()))
    }

    fun writeServerData() {
        val completeObjects = ServerCacheManager.getObjects()
        val jsonDir = NPC_LOCATION.resolve("npcs-server-json").apply { createDirectories() }

        NPC_LOCATION.resolve("npcs-complete.json").toFile()
            .writeText(gson.toJson(completeObjects))

        completeObjects.forEach { (id, objectDef) ->
            jsonDir.resolve("${id}.json").toFile().writeText(gson.toJson(objectDef))
        }
    }

    private fun downloadExamineCsv() {
        val url = URL("https://raw.githubusercontent.com/Joshua-F/osrs-examines/refs/heads/master/npcs.csv")
        val targetPath = NPC_LOCATION.resolve("npc-examines.csv")

        logger.info { "Downloading npcs.csv from GitHub..." }
        url.openStream().use { input ->
            Files.copy(input, targetPath, StandardCopyOption.REPLACE_EXISTING)
        }
        logger.info { "npcs.csv downloaded to $targetPath" }
    }

}