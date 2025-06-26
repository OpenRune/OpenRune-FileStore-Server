package dev.openrune.server.fg.usersimpl

import dev.openrune.server.fg.backend.*

class ObjectServerCodec(private val revision: Int) : CacheDefinitionCodec<ObjectServerType> {

    override val opcodes = OpcodeList<ObjectServerType>().apply {
        add(DefinitionOpcode(3, OpcodeType.INT, ObjectServerType::width))
    }

    override fun createDefinition(): ObjectServerType = ObjectServerType()

}