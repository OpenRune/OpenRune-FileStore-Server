package dev.openrune.server

import com.google.gson.GsonBuilder
import dev.openrune.OsrsCacheProvider
import dev.openrune.cache.CacheStore
import dev.openrune.definition.Definition
import dev.openrune.definition.type.DBRowType
import dev.openrune.definition.type.DBTableType
import dev.openrune.definition.type.EnumType
import dev.openrune.definition.type.HealthBarType
import dev.openrune.definition.type.HitSplatType
import dev.openrune.definition.type.ItemType
import dev.openrune.definition.type.NpcType
import dev.openrune.definition.type.ObjectType
import dev.openrune.definition.type.SequenceType
import dev.openrune.definition.type.StructType
import dev.openrune.definition.type.VarBitType
import dev.openrune.definition.type.VarpType
import dev.openrune.server.impl.item.ItemServerType
import dev.openrune.wiki.dumpers.impl.InfoBoxItem
import dev.openrune.filesystem.Cache
import dev.openrune.server.impl.ObjectServerType
import dev.openrune.server.impl.item.ItemRenderDataManager
import dev.openrune.wiki.dumpers.impl.InfoBoxObject
import java.io.File
import java.nio.file.Path

object ServerCacheManager {

    private val combinedNpcs = mutableMapOf<Int, NpcType>()
    private val combinedObjects = mutableMapOf<Int, ObjectServerType>()
    private val combinedItems = mutableMapOf<Int, ItemServerType>()
    private val combinedVarbits = mutableMapOf<Int, VarBitType>()
    private val combinedVarps = mutableMapOf<Int, VarpType>()
    private val combinedAnims = mutableMapOf<Int, SequenceType>()
    private val combinedEnums = mutableMapOf<Int, EnumType>()
    private val combinedHealthBars = mutableMapOf<Int, HealthBarType>()
    private val combinedHitsplats = mutableMapOf<Int, HitSplatType>()
    private val combinedStructs = mutableMapOf<Int, StructType>()
    private val combinedDbrows = mutableMapOf<Int, DBRowType>()
    private val combinedDbtables = mutableMapOf<Int, DBTableType>()


    @JvmStatic

    fun init(items: Path, objects : Path, vararg dataSources: CacheStore) {
        ItemRenderDataManager.init()
        val itemsData = InfoBoxItem.load(items)
        val objectData = InfoBoxObject.load(objects)

        for (data in dataSources) {
            data.init()

            combinedNpcs.putAll(applyIdOffset(data.npcs, data.npcOffset))
            combinedVarbits.putAll(applyIdOffset(data.varbits, data.varbitOffset))
            combinedVarps.putAll(applyIdOffset(data.varps, data.varpOffset))
            combinedAnims.putAll(applyIdOffset(data.anims, data.animOffset))
            combinedEnums.putAll(applyIdOffset(data.enums, data.enumOffset))
            combinedHealthBars.putAll(applyIdOffset(data.healthBars, data.healthBarOffset))
            combinedHitsplats.putAll(applyIdOffset(data.hitsplats, data.hitsplatOffset))
            combinedStructs.putAll(applyIdOffset(data.structs, data.structOffset))
            combinedDbrows.putAll(applyIdOffset(data.dbrows, data.dbrowOffset))
            combinedDbtables.putAll(applyIdOffset(data.dbtables, data.dbtableOffset))

            combinedItems.putAll(data.items
                .mapKeys { it.key + data.itemOffset }
                .mapValues { ItemServerType.load(it.key, itemsData[it.key], it.value)})

            combinedObjects.putAll(data.objects
                .mapKeys { it.key + data.objectOffset }
                .mapValues { ObjectServerType.load(it.key, objectData[it.key], it.value)})

        }
        ItemRenderDataManager.clear()
    }

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

    private fun <T> getOrDefault(map: Map<Int, T>, id: Int, default: T, typeName: String): T {
        return map.getOrDefault(id, default).also {
            if (id == -1) println("$typeName with id $id is missing.")
        }
    }

    fun getNpc(id: Int) = combinedNpcs[id]
    fun getObject(id: Int) = combinedObjects[id]
    fun getItem(id: Int) = combinedItems[id]
    fun getVarbit(id: Int) = combinedVarbits[id]
    fun getVarp(id: Int) = combinedVarps[id]
    fun getAnim(id: Int) = combinedAnims[id]
    fun getEnum(id: Int) = combinedEnums[id]
    fun getHealthBar(id: Int) = combinedHealthBars[id]
    fun getHitsplat(id: Int) = combinedHitsplats[id]
    fun getStruct(id: Int) = combinedStructs[id]
    fun getDbrow(id: Int) = combinedDbrows[id]
    fun getDbtable(id: Int) = combinedDbtables[id]

    fun getNpcOrDefault(id: Int) = getOrDefault(combinedNpcs, id, NpcType(), "Npc")
    fun getObjectOrDefault(id: Int) = getOrDefault(combinedObjects, id, ObjectType(), "Object")

    fun getItemOrDefault(id: Int) = getOrDefault(combinedItems, id, ItemType(), "Item")
    fun getVarbitOrDefault(id: Int) = getOrDefault(combinedVarbits, id, VarBitType(), "Varbit")
    fun getVarpOrDefault(id: Int) = getOrDefault(combinedVarps, id, VarpType(), "Varp")
    fun getAnimOrDefault(id: Int) = getOrDefault(combinedAnims, id, SequenceType(), "Anim")
    fun getEnumOrDefault(id: Int) = getOrDefault(combinedEnums, id, EnumType(), "Enum")
    fun getHealthBarOrDefault(id: Int) = getOrDefault(combinedHealthBars, id, HealthBarType(), "HealthBar")
    fun getHitsplatOrDefault(id: Int) = getOrDefault(combinedHitsplats, id, HitSplatType(), "Hitsplat")
    fun getStructOrDefault(id: Int) = getOrDefault(combinedStructs, id, StructType(), "Struct")
    fun getDbrowOrDefault(id: Int) = getOrDefault(combinedDbrows, id, DBRowType(), "DBRow")
    fun getDbtableOrDefault(id: Int) = getOrDefault(combinedDbtables, id, DBTableType(), "DBTable")

    // Size methods
    fun npcSize() = combinedNpcs.size
    fun objectSize() = combinedObjects.size
    fun itemSize() = combinedItems.size
    fun varbitSize() = combinedVarbits.size
    fun varpSize() = combinedVarps.size
    fun animSize() = combinedAnims.size
    fun enumSize() = combinedEnums.size
    fun healthBarSize() = combinedHealthBars.size
    fun hitsplatSize() = combinedHitsplats.size
    fun structSize() = combinedStructs.size

    // Bulk getters
    fun getNpcs() = combinedNpcs.toMap()
    fun getObjects() = combinedObjects.toMap()
    fun getItems() = combinedItems.toMap()
    fun getVarbits() = combinedVarbits.toMap()
    fun getVarps() = combinedVarps.toMap()
    fun getAnims() = combinedAnims.toMap()
    fun getEnums() = combinedEnums.toMap()
    fun getHealthBars() = combinedHealthBars.toMap()
    fun getHitsplats() = combinedHitsplats.toMap()
    fun getStructs() = combinedStructs.toMap()


}

fun main(args: Array<String>) {
    ServerCacheManager.init(
        Path.of("./dumps/230/items.json"),
        Path.of("./dumps/230/objects.json"),
        OsrsCacheProvider(Cache.load(Path.of("E:\\RSPS\\Illerai\\Cadarn-Server\\data\\cache"), false), 230)
    )
}
