package dev.openrune.server.fg.backend

import dev.openrune.definition.Definition
import dev.openrune.definition.DefinitionCodec
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

abstract class CacheDefinitionCodec<T : Definition> : DefinitionCodec<T> {
    abstract val opcodes: OpcodeList<T>

    override fun T.read(opcode: Int, buffer: ByteBuf) {
        val defOpcode = opcodes.allOpcodes.firstOrNull { it.opcode == opcode }
            ?: error("Unknown opcode $opcode for ${this::class.simpleName}")
        defOpcode.decode(buffer, this)
    }

    override fun ByteBuf.encode(definition: T) {
        for (opcodeDef in opcodes.allOpcodes) {
            if (opcodeDef.shouldEncode(definition)) {
                opcodeDef.encode(this, definition)
            }
        }
        writeByte(0)
    }

}