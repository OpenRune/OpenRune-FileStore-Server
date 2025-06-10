package dev.openrune.server

import dev.openrune.cache.CacheStore
import java.nio.file.Path

data class ServerCacheConfig(
    val itemsPath: Path? = null,
    val objectsPath: Path? = null,
    val dataStore: CacheStore
)