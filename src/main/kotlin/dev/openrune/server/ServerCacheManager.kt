package dev.openrune.dev.openrune.server

import dev.openrune.OsrsCacheProvider
import dev.openrune.cache.CacheManager
import dev.openrune.cache.CacheStore
import dev.openrune.definition.Definition
import dev.openrune.server.impl.ItemServerType
import dev.openrune.dev.openrune.wiki.dumpers.impl.InfoBoxItem
import dev.openrune.filesystem.Cache
import java.nio.file.Path

object ServerCacheManager {

    private val combinedItems = mutableMapOf<Int, ItemServerType>()

    @JvmStatic
    fun init(dataSources : CacheStore,items : Path) {
        CacheManager.init(dataSources)

        val itemsData = InfoBoxItem.load(items)
        CacheManager.getItems().forEach { (key, value) -> combinedItems[key] = ItemServerType.load(key, itemsData[key], value) }
        println(combinedItems.size)
    }

    fun getItem(id: Int) = combinedItems[id]
    fun getItemOrDefault(id: Int) = getOrDefault(combinedItems, id, ItemServerType(), "Item")

    private fun <T : Definition> applyIdOffset(definitions: MutableMap<Int, T>, offset: Int): MutableMap<Int, T> {
        return if (offset != 0) {
            definitions.mapKeys { (key, definition) ->
                val newKey = key + offset
                definition.id = newKey
                newKey
            }.toMutableMap()
        } else {
            definitions.toMutableMap()
        }
    }

    private inline fun <T> getOrDefault(map: Map<Int, T>, id: Int, default: T, typeName: String): T {
        return map.getOrDefault(id, default).also {
            if (id == -1) println("$typeName with id $id is missing.")
        }
    }

}

fun main(args: Array<String>) {
    ServerCacheManager.init(
        OsrsCacheProvider(Cache.load(Path.of("E:\\RSPS\\Illerai\\Cadarn-Server\\data\\cache"), false),230),
        Path.of("./items.json")
    )
    println(ServerCacheManager.getItemOrDefault(995).name)
}
