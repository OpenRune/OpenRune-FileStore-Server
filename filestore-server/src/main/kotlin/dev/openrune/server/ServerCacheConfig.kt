package dev.openrune.server

import dev.openrune.cache.CacheStore
import java.nio.file.Path

data class ServerCacheConfig(
    val itemPaths: List<Path> = emptyList(),
    val objectPaths: List<Path> = emptyList(),
    val npcPaths: List<Path> = emptyList(),
    val dataStore: CacheStore
)