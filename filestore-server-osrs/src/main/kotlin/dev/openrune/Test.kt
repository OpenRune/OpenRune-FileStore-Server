package dev.openrune.server

import com.displee.cache.CacheLibrary
import dev.openrune.OsrsCacheProvider
import dev.openrune.ServerCacheManager
import dev.openrune.cache.CONFIGS
import dev.openrune.cache.CacheDelegate
import dev.openrune.cache.CacheManager
import dev.openrune.definition.*
import dev.openrune.definition.codec.*
import dev.openrune.server.definition.codec.ConfigDefinitionDecoder

fun main(args: Array<String>) {

    val cache = CacheDelegate(CacheLibrary("E:\\RSPS\\Hazy\\HazyGameServer\\data\\cache"))
    CacheManager.init(OsrsCacheProvider(cache,231))

    println(CacheManager.getObject(29662))

    val codec = ObjectServerCodec(CacheManager.getObjects())
    val codec2 = HealthBarServerCodec(CacheManager.getHealthBars())
    val codec3 = SequenceServerCodec(CacheManager.getAnims())
    val codec4 = NpcServerCodec(CacheManager.getNpcs())
    val codec5 = ItemServerCodec(CacheManager.getItems())

    CacheManager.getObjects().forEach {
        cache.write(CONFIGS,55,it.key,codec.encodeToBuffer(ObjectServerType(it.key)))
    }

    CacheManager.getHealthBars().forEach {
        cache.write(CONFIGS,56,it.key,codec2.encodeToBuffer(HealthBarServerType(it.key)))
    }

    CacheManager.getAnims().forEach {
        cache.write(CONFIGS,57,it.key,codec3.encodeToBuffer(SequenceServerType(it.key)))
    }


    CacheManager.getNpcs().forEach {
        cache.write(CONFIGS,58,it.key,codec4.encodeToBuffer(NpcServerType(it.key)))
    }


    CacheManager.getItems().forEach {
        cache.write(CONFIGS,59,it.key,codec5.encodeToBuffer(ItemServerType(it.key)))
    }



    cache.library.update()

    ServerCacheManager.init(cache)

    ServerCacheManager.getItems().filter { it.value.name.contains("longsword") }.forEach {
        println(it.value.equipment?.stats)
    }


}
