package dev.openrune.tools

import dev.openrune.OsrsCacheProvider
import dev.openrune.cache.CONFIGS
import dev.openrune.cache.CacheManager
import dev.openrune.cache.tools.TaskPriority
import dev.openrune.cache.tools.tasks.CacheTask
import dev.openrune.definition.*
import dev.openrune.definition.codec.*
import dev.openrune.filesystem.Cache
import dev.openrune.server.infobox.InfoBoxItem
import dev.openrune.server.infobox.Load
import io.github.oshai.kotlinlogging.KotlinLogging


private val logger = KotlinLogging.logger {}

class PackServerConfig : CacheTask() {

    override val priority: TaskPriority
        get() = TaskPriority.END

    override fun init(cache: Cache) {
        logger.info { "Packing server configurations..." }

        CacheManager.init(OsrsCacheProvider(cache,revision))

        val codec = ObjectServerCodec(CacheManager.getObjects())
        val codec2 = HealthBarServerCodec(CacheManager.getHealthBars())
        val codec3 = SequenceServerCodec(CacheManager.getAnims())
        val codec4 = NpcServerCodec(CacheManager.getNpcs())
        val codec5 = ItemServerCodec(CacheManager.getItems(),CacheManager.getEnums(), InfoBoxItem.load(Load.getDefaultResourceTempFile("items.json")))
        
        logger.info { "Packing Objects..." }
        CacheManager.getObjects().forEach {
            cache.write(CONFIGS, 55, it.key, codec.encodeToBuffer(ObjectServerType(it.key)))
        }

        logger.info { "Packing Health Bars..." }
        CacheManager.getHealthBars().forEach {
            cache.write(CONFIGS, 56, it.key, codec2.encodeToBuffer(HealthBarServerType(it.key)))
        }

        logger.info { "Packing Animations..." }
        CacheManager.getAnims().forEach {
            cache.write(CONFIGS, 57, it.key, codec3.encodeToBuffer(SequenceServerType(it.key)))
        }

        logger.info { "Packing NPCs..." }
        CacheManager.getNpcs().forEach {
            cache.write(CONFIGS, 58, it.key, codec4.encodeToBuffer(NpcServerType(it.key)))
        }

        logger.info { "Packing Items..." }
        CacheManager.getItems().forEach {
            cache.write(CONFIGS, 59, it.key, codec5.encodeToBuffer(ItemServerType(it.key)))
        }

        logger.info { "Finished packing all server configurations." }
    }
}
