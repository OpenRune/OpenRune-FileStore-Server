package dev.openrune.server.fg.backend

import dev.openrune.definition.Definition
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

interface CacheDefinitionCodec<T : Definition> {

    fun readLoop(definition: T, buffer: ByteBuf) {
        while (true) {
            val opcode = buffer.readUnsignedByte().toInt()
            if (opcode == 0) {
                break
            }
            definition.read(opcode, buffer)
        }
    }

    fun T.read(opcode: Int, buffer: ByteBuf)
    fun ByteBuf.encode(definition: T)

    fun createDefinition(): T

    fun loadData(id: Int, data: ByteArray?): T {
        val definition = createDefinition()
        definition.id = id
        if(data != null && data.size > 0) {
            val reader = Unpooled.wrappedBuffer(data)
            try {
                readLoop(definition, reader)
            }catch (e: Exception) {
                error("Unable to decode ${definition.javaClass.simpleName} [$id]")
            }
        }
        return definition
    }
}
