package dev.openrune.wiki

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.moandjiezana.toml.TomlWriter
import dev.openrune.OsrsCacheProvider
import dev.openrune.cache.CacheManager
import dev.openrune.cache.tools.*
import dev.openrune.cache.tools.OpenRS2.allCaches
import dev.openrune.cache.util.stringToTimestamp
import dev.openrune.cache.util.toEchochUTC
import dev.openrune.filesystem.Cache
import dev.openrune.server.infobox.InfoBoxItem
import dev.openrune.wiki.dumpers.impl.*
import io.github.oshai.kotlinlogging.KotlinLogging
import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarBuilder
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object WikiDumper {

    private val logger = KotlinLogging.logger {}

    var rev = -1

    var getBaseLocation = File("./dumps/$rev")

    var wikiLocation = File(getBaseLocation,"wiki/wiki.xml")

    val wiki : Wiki get() = Wiki.load(wikiLocation.path)

    @JvmStatic
    fun main(args: Array<String>) {
        if (rev == -1) {
            logger.info { "Finding OSRS Latest Rev" }
            loadCaches()
            rev = getLatest(allCaches).builds.first().major
            logger.info { "Found Rev: $rev" }
            getBaseLocation = File("./dumps/$rev")
            wikiLocation = File(getBaseLocation,"wiki/wiki.xml")

        }

        setup(encodingSettings = EncodingSettings(encodeType = FileType.JSON, prettyPrint = true, linkedIds = false))
    }


    fun setup(
        cache : File = File(getBaseLocation,"cache"),
        encodingSettings: EncodingSettings = EncodingSettings()
    ) {

        if (!getBaseLocation.exists()) {
            getBaseLocation.mkdirs()
        }

        if (!wikiLocation.exists()) {
            logger.info { "Wiki Data does not Exist Downloading" }
            File(getBaseLocation,"wiki").mkdirs()
            RunescapeWikiExporter.export(wikiLocation.parentFile)
        }

        if (!cache.exists()) {
            OpenRS2.downloadCacheByRevision(rev,cache, listener =  object : DownloadListener {
                var progressBar: ProgressBar? = null

                override fun onProgress(progress: Int, max: Long, current: Long) {
                    if (progressBar == null) {
                        progressBar = ProgressBarBuilder().setTaskName("Downloading Cache").setInitialMax(max).build()
                    } else {
                        progressBar?.stepTo(current)
                    }
                }

                override fun onError(exception: Exception) {
                    error("Error Downloading: $exception")
                }

                override fun onFinished() {
                    progressBar?.close()
                    val zipLoc = File(cache, "disk.zip")
                    if (unzip(zipLoc, cache)) {
                        logger.info { "Cache downloaded and unzipped successfully." }
                        zipLoc.delete()
                    } else {
                        error("Error Unzipping")
                    }
                }
            })
        }
    }

    fun unzip(zipFile: File, destDir: File): Boolean {
        return try {
            if (!destDir.exists()) destDir.mkdirs()

            val zipInputStream = ZipInputStream(FileInputStream(zipFile))
            var zipEntry: ZipEntry?

            while (zipInputStream.nextEntry.also { zipEntry = it } != null) {
                val outputFile = File(destDir, zipEntry!!.name.substringAfterLast('/'))
                if (!zipEntry!!.isDirectory) {
                    outputFile.parentFile?.mkdirs()
                    FileOutputStream(outputFile).use { fileOutputStream ->
                        zipInputStream.copyTo(fileOutputStream)
                    }
                }
                zipInputStream.closeEntry()
            }
            zipInputStream.close()
            logger.info { "Unzipped successfully" }
            true
        } catch (e: IOException) {
            logger.error { "Error while unzipping: ${e.message}" }
            false
        }
    }

    private const val CACHE_DOWNLOAD_LOCATION = "https://archive.openrs2.org/caches.json"

    private fun loadCaches() {
        if (allCaches.isEmpty()) {
            val json = URL(CACHE_DOWNLOAD_LOCATION).readText()
            allCaches = Gson().fromJson(json, Array<CacheInfo>::class.java)
        }
    }



    private fun getLatest(caches: Array<CacheInfo>, game: GameType = GameType.OLDSCHOOL) =
        caches
            .filter { it.game.contains(game.formatName()) }
            .filter { it.builds.isNotEmpty() }
            .filter { it.timestamp != null }
            .filter { it.environment == "live" }
            .maxByOrNull { it.timestamp.stringToTimestamp().toEchochUTC() }
            ?: error("Unable to find Latest Revision")


}

fun writeData(encodingSettings: EncodingSettings, data : Any,path : File) {
    when (encodingSettings.encodeType) {
        FileType.JSON -> {
            val gsonBuilder = GsonBuilder().apply {
                if (encodingSettings.prettyPrint) {
                    setPrettyPrinting()
                }
                registerTypeAdapter(InfoBoxItem::class.java, InfoBoxItemSerializer())
            }
            File("$path.json").writeText(gsonBuilder.create().toJson(data))
        }
        FileType.TOML -> {
            val tomlWriter = TomlWriter()
            tomlWriter.write(data, File("$path.json"))
        }
        else -> { }
    }

}