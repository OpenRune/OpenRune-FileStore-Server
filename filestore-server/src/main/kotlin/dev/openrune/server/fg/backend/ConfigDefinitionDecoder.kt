package dev.openrune.server.fg.backend

import dev.openrune.cache.CONFIGS
import dev.openrune.cache.filestore.definition.DefinitionDecoder
import dev.openrune.cache.filestore.definition.DefinitionTransform
import dev.openrune.definition.Definition
import dev.openrune.definition.DefinitionCodec

abstract class ConfigDefinitionDecoder<T : Definition>(
    codec: CacheDefinitionCodec<T>,
    private val archive: Int,
    transform: DefinitionTransform<T>? = null
) : ServerDefinitionDecoder<T>(CONFIGS, codec, transform) {

    override fun getArchive(id: Int) = archive

    override fun getFile(id: Int) = id
}
