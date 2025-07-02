package dev.openrune.wiki.dumpers

import com.google.gson.GsonBuilder
import dev.openrune.cache.CacheManager
import dev.openrune.wiki.DATA_LOCATION
import dev.openrune.wiki.EncodingSettings
import io.github.oshai.kotlinlogging.KotlinLogging
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.createDirectories

object Objects {

    private val logger = KotlinLogging.logger {}
    private val gson = GsonBuilder().setPrettyPrinting().create()
    var OBJECTS_LOCATION = DATA_LOCATION.resolve("objects")

    fun init() {
        OBJECTS_LOCATION = DATA_LOCATION.resolve("objects")

        OBJECTS_LOCATION.createDirectories()
        val encodingSettings = EncodingSettings(linkedIds = false)

        parseObjects(encodingSettings)
        downloadExamineCsv()
    }

    private fun parseObjects(settings: EncodingSettings) {
        OBJECTS_LOCATION.resolve("objects-cache-only.json").toFile()
            .writeText(gson.toJson(CacheManager.getObjects()))

    }

    fun writeServerData() {
        val completeObjects = CacheManager.getObjects()
        val jsonDir = OBJECTS_LOCATION.resolve("objects-server-json").apply { createDirectories() }

        OBJECTS_LOCATION.resolve("objects-complete.json").toFile()
            .writeText(gson.toJson(completeObjects))

        completeObjects.forEach { (id, objectDef) ->
            jsonDir.resolve("${id}.json").toFile().writeText(gson.toJson(objectDef))
        }
    }

    private fun downloadExamineCsv() {
        val url = URL("https://raw.githubusercontent.com/Joshua-F/osrs-examines/refs/heads/master/locs.csv")
        val targetPath = OBJECTS_LOCATION.resolve("object-examines.csv")

        logger.info { "Downloading locs.csv from GitHub..." }
        url.openStream().use { input ->
            Files.copy(input, targetPath, StandardCopyOption.REPLACE_EXISTING)
        }
        logger.info { "locs.csv downloaded to $targetPath" }
    }

}