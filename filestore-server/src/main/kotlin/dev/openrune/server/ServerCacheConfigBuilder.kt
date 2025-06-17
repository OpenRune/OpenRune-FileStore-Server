package dev.openrune.server

import dev.openrune.cache.CacheStore
import io.github.oshai.kotlinlogging.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path

const val LAST_UPDATED_REV = 230

class ServerCacheConfigBuilder {

    private val logger = KotlinLogging.logger {}

    private val itemPaths = mutableListOf<Path>()
    private val objectPaths = mutableListOf<Path>()
    private val npcPaths = mutableListOf<Path>()

    private var _dataStore: CacheStore? = null
    var dataStore: CacheStore
        get() = _dataStore ?: error("CacheStore has not been set.")
        set(value) { _dataStore = value }

    fun addItemPath(path: Path) = apply { itemPaths.add(path) }
    fun addObjectPath(path: Path) = apply { objectPaths.add(path) }
    fun addNpcPath(path: Path) = apply { npcPaths.add(path) }

    fun resetItemPaths() = apply { itemPaths.clear() }
    fun resetObjectPaths() = apply { objectPaths.clear() }
    fun resetNpcPaths() = apply { npcPaths.clear() }

    fun setItemPaths(vararg paths: Path) = apply {
        itemPaths.clear()
        itemPaths.addAll(paths)
    }

    fun setObjectPaths(vararg paths: Path) = apply {
        objectPaths.clear()
        objectPaths.addAll(paths)
    }

    fun setNpcPaths(vararg paths: Path) = apply {
        npcPaths.clear()
        npcPaths.addAll(paths)
    }

    fun build(): ServerCacheConfig {
        val store = _dataStore ?: error("You must set a CacheStore using `dataStore = ...`.")
        val storeRev = store.cacheRevision

        val usingDefaults = itemPaths.isEmpty() || objectPaths.isEmpty()

        if (usingDefaults && storeRev > LAST_UPDATED_REV) {
            logger.info {
                """
                ⚠️  The cache revision ($storeRev) is newer than the embedded resources (LAST_UPDATED_REV = $LAST_UPDATED_REV).
                    Consider updating items.json and/or objects.json using the wiki dumper or try bumping the library revision.
                """.trimIndent()
            }
        }

        return ServerCacheConfig(
            itemPaths = if (itemPaths.isNotEmpty()) itemPaths.toList() else listOf(getDefaultResourceTempFile("items.json")),
            objectPaths = if (objectPaths.isNotEmpty()) objectPaths.toList() else listOf(getDefaultResourceTempFile("object-examines.csv")),
            npcPaths = if (npcPaths.isNotEmpty()) npcPaths.toList() else listOf(getDefaultResourceTempFile("npcs.json")),
            dataStore = store
        )
    }

    private fun getDefaultResourceTempFile(fileName: String): Path {
        val inputStream = javaClass.getResourceAsStream("/$fileName")
            ?: error("Missing default resource: /$fileName")

        val tempFile = Files.createTempFile("default_resource_", "_$fileName")
        inputStream.use { input ->
            Files.newOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        tempFile.toFile().deleteOnExit()
        return tempFile
    }
}
