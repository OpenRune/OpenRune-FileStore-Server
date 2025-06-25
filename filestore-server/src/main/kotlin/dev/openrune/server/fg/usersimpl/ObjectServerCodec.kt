package dev.openrune.server.fg.usersimpl

import dev.openrune.server.fg.backend.CacheDefinitionCodec
import io.netty.buffer.ByteBuf

class ObjectServerCodec(private val revision: Int) : CacheDefinitionCodec<ObjectServerType> {
    //ABLE TP PASS IN THEIR OWN DATA FROM JSON OR W/E IN SIDE HERE TO READ OR TO ENCODE


    override fun ObjectServerType.read(opcode: Int, buffer: ByteBuf) {
        TODO("Not yet implemented")
    }

    override fun ByteBuf.encode(definition: ObjectServerType) {
        TODO("Not yet implemented")
    }


    override fun createDefinition(): ObjectServerType = ObjectServerType(1)

}