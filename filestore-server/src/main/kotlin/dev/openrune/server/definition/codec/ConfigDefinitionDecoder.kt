package dev.openrune.server.definition.codec

import dev.openrune.Index.CONFIGS
import dev.openrune.cache.filestore.definition.DefinitionDecoder
import dev.openrune.cache.filestore.definition.DefinitionTransform
import dev.openrune.definition.Definition

abstract class ConfigDefinitionDecoder<T : Definition>(
    codec: CacheDefinitionCodec<T>,
    private val archive: Int,
    transform: DefinitionTransform<T>? = null
) : DefinitionDecoder<T>(CONFIGS, codec, transform) {

    override fun getArchive(id: Int) = archive

    override fun getFile(id: Int) = id
}