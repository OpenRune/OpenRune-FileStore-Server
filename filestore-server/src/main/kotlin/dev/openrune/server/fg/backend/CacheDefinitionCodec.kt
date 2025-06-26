package dev.openrune.server.fg.backend

import dev.openrune.definition.Definition
import dev.openrune.server.fg.usersimpl.ObjectServerType
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

interface CacheDefinitionCodec<T : Definition> {
    val opcodes: OpcodeList<T>

    fun readLoop(definition: T, buffer: ByteBuf) {
        while (true) {
            val opcode = buffer.readUnsignedByte().toInt()
            if (opcode == 0) break
            val defOpcode = opcodes.allOpcodes.first { it.opcode == opcode }
            defOpcode.decode(buffer, definition)
        }
    }

    fun ByteBuf.encode(definition: T) {
        for (opcodeDef in opcodes.allOpcodes) {
            if (opcodeDef.shouldEncode(definition)) {
                opcodeDef.encode(this, definition)
            }
        }
        writeByte(0)
    }

    fun createDefinition(): T

    fun loadData(id: Int, data: ByteArray?): T {
        val definition = createDefinition()
        definition.id = id
        if (data != null && data.isNotEmpty()) {
            println("Byters: ${data!!.size}")
            val reader = Unpooled.wrappedBuffer(data)
            try {
                readLoop(definition, reader)
            } catch (e: Exception) {
                e.printStackTrace()
                error("Unable to decode ${definition.javaClass.simpleName} [$id]: ${e.message}")
            }
        }
        return definition
    }
}