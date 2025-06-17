 package dev.openrune.server

import dev.openrune.cache.CacheStore
import dev.openrune.cache.getOrDefault
import dev.openrune.definition.type.*
import dev.openrune.filesystem.Cache
import dev.openrune.server.impl.NpcServerType
import dev.openrune.server.impl.ObjectServerType
import dev.openrune.server.impl.item.ItemRenderDataManager
import dev.openrune.server.impl.item.ItemServerType
import dev.openrune.server.infobox.InfoBoxItem
import dev.openrune.server.infobox.InfoBoxNpc
import dev.openrune.server.infobox.InfoBoxObject

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
        val store = config.dataStore

        ItemRenderDataManager.init()
        val itemsData = config.itemPaths.flatMap { InfoBoxItem.load(it).entries }.associate { it.key to it.value }
        val objectData = config.objectPaths.flatMap { InfoBoxObject.load(it).entries }.associate { it.key to it.value }
        val npcData = config.npcPaths.flatMap { InfoBoxNpc.load(it).entries }.associate { it.key to it.value }

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


        items.putAll(store.items.mapValues { ItemServerType.load(it.key, itemsData[it.key], it.value) })
        npcs.putAll(store.npcs.mapValues { NpcServerType.load(it.key, npcData[it.key], it.value) })
        objects.putAll(store.objects.mapValues { ObjectServerType.load(it.key, objectData[it.key],store.objects) })

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