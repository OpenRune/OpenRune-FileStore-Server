 package dev.openrune.server

import dev.openrune.OsrsCacheProvider
import dev.openrune.cache.CacheStore
import dev.openrune.cache.getOrDefault
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
import dev.openrune.server.ServerCacheManager.buildServerCacheConfig
import dev.openrune.server.impl.NpcServerType
import dev.openrune.server.impl.ObjectServerType
import dev.openrune.server.impl.item.ItemRenderDataManager
import dev.openrune.wiki.dumpers.impl.InfoBoxObject
import java.nio.file.Path

object ServerCacheManager {

    fun buildServerCacheConfig(init: ServerCacheConfigBuilder.() -> Unit): ServerCacheConfig {
        return ServerCacheConfigBuilder().apply(init).build()
    }

    private val npcs = mutableMapOf<Int, NpcServerType>()
    private val objects = mutableMapOf<Int, ObjectServerType>()
    private val items = mutableMapOf<Int, ItemServerType>()
    private val varbits = mutableMapOf<Int, VarBitType>()
    private val varps = mutableMapOf<Int, VarpType>()
    private val anims = mutableMapOf<Int, SequenceType>()
    private val enums = mutableMapOf<Int, EnumType>()
    private val healthBars = mutableMapOf<Int, HealthBarType>()
    private val hitsplats = mutableMapOf<Int, HitSplatType>()
    private val structs = mutableMapOf<Int, StructType>()
    private val dbrows = mutableMapOf<Int, DBRowType>()
    private val dbtables = mutableMapOf<Int, DBTableType>()

    fun init(config: ServerCacheConfig) {
        val itemsPath = config.itemsPath!!
        val objectsPath = config.objectsPath!!
        val store = config.dataStore

        ItemRenderDataManager.init()
        val itemsData = InfoBoxItem.load(itemsPath)
        val objectData = InfoBoxObject.load(objectsPath)

        store.init()

        varbits.putAll(store.varbits)
        varps.putAll(store.varps)
        anims.putAll(store.anims)
        enums.putAll(store.enums)
        healthBars.putAll(store.healthBars)
        hitsplats.putAll(store.hitsplats)
        structs.putAll(store.structs)
        dbrows.putAll(store.dbrows)
        dbtables.putAll(store.dbtables)


        this.items.putAll(store.items.mapValues { ItemServerType.load(it.key, itemsData[it.key], it.value) })
        this.npcs.putAll(store.npcs.mapValues { NpcServerType.load(it.key, itemsData[it.key], it.value) })
        this.objects.putAll(store.objects.mapValues { ObjectServerType.load(it.key, objectData[it.key],store.objects) })

        ItemRenderDataManager.clear()
    }

    fun getNpc(id: Int) = npcs[id]
    fun getObject(id: Int) = objects[id]
    fun getItem(id: Int) = items[id]
    fun getVarbit(id: Int) = varbits[id]
    fun getVarp(id: Int) = varps[id]
    fun getAnim(id: Int) = anims[id]
    fun getEnum(id: Int) = enums[id]
    fun getHealthBar(id: Int) = healthBars[id]
    fun getHitsplat(id: Int) = hitsplats[id]
    fun getStruct(id: Int) = structs[id]
    fun getDbrow(id: Int) = dbrows[id]
    fun getDbtable(id: Int) = dbtables[id]

    fun getNpcOrDefault(id: Int) = getOrDefault(npcs, id, NpcType(), "Npc")
    fun getObjectOrDefault(id: Int) = getOrDefault(objects, id, ObjectType(), "Object")

    fun getItemOrDefault(id: Int) = getOrDefault(items, id, ItemType(), "Item")
    fun getVarbitOrDefault(id: Int) = getOrDefault(varbits, id, VarBitType(), "Varbit")
    fun getVarpOrDefault(id: Int) = getOrDefault(varps, id, VarpType(), "Varp")
    fun getAnimOrDefault(id: Int) = getOrDefault(anims, id, SequenceType(), "Anim")
    fun getEnumOrDefault(id: Int) = getOrDefault(enums, id, EnumType(), "Enum")
    fun getHealthBarOrDefault(id: Int) = getOrDefault(healthBars, id, HealthBarType(), "HealthBar")
    fun getHitsplatOrDefault(id: Int) = getOrDefault(hitsplats, id, HitSplatType(), "Hitsplat")
    fun getStructOrDefault(id: Int) = getOrDefault(structs, id, StructType(), "Struct")
    fun getDbrowOrDefault(id: Int) = getOrDefault(dbrows, id, DBRowType(), "DBRow")
    fun getDbtableOrDefault(id: Int) = getOrDefault(dbtables, id, DBTableType(), "DBTable")

    // Size methods
    fun npcSize() = npcs.size
    fun objectSize() = objects.size
    fun itemSize() = items.size
    fun varbitSize() = varbits.size
    fun varpSize() = varps.size
    fun animSize() = anims.size
    fun enumSize() = enums.size
    fun healthBarSize() = healthBars.size
    fun hitsplatSize() = hitsplats.size
    fun structSize() = structs.size

    // Bulk getters
    fun getNpcs() = npcs.toMap()
    fun getObjects() = objects.toMap()
    fun getItems() = items.toMap()
    fun getVarbits() = varbits.toMap()
    fun getVarps() = varps.toMap()
    fun getAnims() = anims.toMap()
    fun getEnums() = enums.toMap()
    fun getHealthBars() = healthBars.toMap()
    fun getHitsplats() = hitsplats.toMap()
    fun getStructs() = structs.toMap()


}

fun main(args: Array<String>) {

    val cache = Cache.load(Path.of("E:\\RSPS\\Illerai\\Cadarn-Server\\data\\cache"), false)
    val config = buildServerCacheConfig {
        dataStore =  OsrsCacheProvider(cache, 230)
    }

    ServerCacheManager.init(config)
}
