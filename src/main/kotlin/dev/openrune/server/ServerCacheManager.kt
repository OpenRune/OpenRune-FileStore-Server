package dev.openrune.server

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.openrune.OsrsCacheProvider
import dev.openrune.cache.CacheManager
import dev.openrune.cache.CacheStore
import dev.openrune.definition.Definition
import dev.openrune.server.ServerCacheManager.getItem
import dev.openrune.server.impl.ItemServerType
import dev.openrune.wiki.dumpers.impl.InfoBoxItem
import dev.openrune.filesystem.Cache
import dev.openrune.server.impl.ItemRenderDataManager
import org.openjdk.jol.info.GraphLayout
import java.io.File
import java.nio.file.Path

object ServerCacheManager {

    private val combinedItems = mutableMapOf<Int, ItemServerType>()

    @JvmStatic
    fun init(dataSources : CacheStore,items : Path) {
        CacheManager.init(dataSources)
        ItemRenderDataManager.init()

        val itemsData = InfoBoxItem.load(items)
        CacheManager.getItems().forEach { (key, value) -> combinedItems[key] = ItemServerType.load(key, itemsData[key], value) }

        logCombinedItemsMemoryUsage()

        ItemRenderDataManager.clear()
    }
    fun logCombinedItemsMemoryUsage() {
        println("Estimating memory usage of combinedItems...")
        val layout = GraphLayout.parseInstance(combinedItems)
        println("Total size: ${layout.totalSize()} bytes")
        println("Item count: ${combinedItems.size}")
        println("Size per item (approx): ${layout.totalSize() / combinedItems.size} bytes")
    }

    fun getItem(id: Int) = combinedItems[id]
    fun getItems() = combinedItems
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
        OsrsCacheProvider(Cache.load(Path.of("E:\\RSPS\\Illerai\\Cadarn-Server\\data\\cache"), false), 230),
        Path.of("./items.json")
    )

    File("testView.json").writeText(GsonBuilder().setPrettyPrinting().create().toJson(ServerCacheManager.getItems()))
}
