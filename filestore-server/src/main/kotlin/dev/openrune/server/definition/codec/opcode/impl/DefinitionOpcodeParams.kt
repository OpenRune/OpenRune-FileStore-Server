package dev.openrune.server.definition.codec.opcode.impl

import io.netty.buffer.ByteBuf
import kotlin.reflect.KMutableProperty1
import dev.openrune.definition.util.readUnsignedBoolean
import dev.openrune.definition.util.readString
import dev.openrune.definition.util.writeString
import dev.openrune.server.definition.codec.opcode.DefinitionOpcode

fun <T> DefinitionOpcodeParams(
    opcode: Int,
    property: KMutableProperty1<T, MutableMap<String, Any>?>
): DefinitionOpcode<T> = DefinitionOpcode(
    opcode,
    decode = { buf, def, _ ->
        val count = buf.readUnsignedByte().toInt()
        val map = mutableMapOf<String, Any>()
        repeat(count) {
            val isString = buf.readUnsignedBoolean()
            val id = buf.readUnsignedMedium()
            val value = if (isString) buf.readString() else buf.readInt()
            map[id.toString()] = value
        }
        property.set(def, map)
    },
    encode = { buf, def ->
        property.get(def)?.let { params ->
            buf.writeByte(opcode)
            buf.writeByte(params.size)
            for ((id, value) in params) {
                val isString = value is String
                buf.writeByte(if (isString) 1 else 0)
                buf.writeMedium(id.toInt())
                when (value) {
                    is String -> buf.writeString(value)
                    is Int -> buf.writeInt(value)
                    is Long -> {
                        require(value in Int.MIN_VALUE..Int.MAX_VALUE) {
                            "Long value $value is out of Int range for id $id"
                        }
                        buf.writeInt(value.toInt())
                    }
                    else -> error("Unsupported parameter type for id $id: ${value::class}")
                }
            }
        }
    },
    skipByteEncode = true,
    shouldEncode = { property.get(it)?.isNotEmpty() == true }
)