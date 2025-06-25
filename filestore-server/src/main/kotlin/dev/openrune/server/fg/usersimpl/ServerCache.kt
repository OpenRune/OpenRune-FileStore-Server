package dev.openrune.server.fg.usersimpl

import dev.openrune.OsrsCacheProvider.Companion.CACHE_REVISION
import dev.openrune.cache.OBJECT
import dev.openrune.filesystem.Cache
import dev.openrune.server.fg.backend.ConfigDefinitionDecoder
import java.nio.file.Path

class ObjectDecoder : ConfigDefinitionDecoder<ObjectServerType>(ObjectServerCodec(CACHE_REVISION), OBJECT)

//THIS WILL BE MADE BY THE USER SO THEY CAN DECODE WHAT IS LOADED FROM THE CACHE
object ServerCache {

    val objects : MutableMap<Int, ObjectServerType> = emptyMap<Int,ObjectServerType>().toMutableMap()

    fun init() {
        val cache = Cache.load(Path.of(""))
        ObjectDecoder().load(cache, objects)
    }

}