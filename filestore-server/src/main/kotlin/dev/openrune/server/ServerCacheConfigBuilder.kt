package dev.openrune.server

import dev.openrune.cache.CacheStore
import io.github.oshai.kotlinlogging.KotlinLogging
import java.nio.file.Path

const val LAST_UPDATED_REV = 229

class ServerCacheConfigBuilder {

    private val logger = KotlinLogging.logger {}

    var itemsPath: Path? = null
    var objectsPath: Path? = null

    private var _dataStore: CacheStore? = null
    var dataStore: CacheStore
        get() = _dataStore ?: error("CacheStore has not been set.")
        set(value) { _dataStore = value }

    fun build(): ServerCacheConfig {
        val store = _dataStore ?: error("You must set a CacheStore using `dataStore = ...`.")
        val storeRev = store.cacheRevision

        val usingDefaults = itemsPath == null || objectsPath == null

        if (usingDefaults && storeRev > LAST_UPDATED_REV) {
            logger.info {
                """
                ⚠️  The cache revision ($storeRev) is newer than the embedded resources (LAST_UPDATED_REV = $LAST_UPDATED_REV).
                    Consider updating items.json and/or objects.json using the wiki dumper or try bumping the library revision.
                """.trimIndent()
            }
        }

        return ServerCacheConfig(
            itemsPath = itemsPath ?: getDefaultResourcePath("items.json"),
            objectsPath = objectsPath ?: getDefaultResourcePath("objects.json"),
            dataStore = store
        )
    }

    private fun getDefaultResourcePath(fileName: String): Path {
        val resource = javaClass.getResource("/$fileName")
            ?: error("Missing default resource: /$fileName")
        return Path.of(resource.toURI())
    }
}