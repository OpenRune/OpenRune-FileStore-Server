package dev.openrune.server.definition.codec.opcode

import io.netty.buffer.ByteBuf
import dev.openrune.definition.util.readString
import dev.openrune.definition.util.writeString

enum class OpcodeType(
    val read: (ByteBuf) -> Any,
    val write: (ByteBuf, Any) -> Unit
) {
    STRING({ it.readString() }, { buf, v -> buf.writeString(v as String) }),
    BYTE({ it.readByte() }, { buf, v -> buf.writeByte((v as Int)) }),
    SHORT({ it.readShort().toInt() }, { buf, v -> buf.writeShort((v as Int)) }),
    USHORT({ it.readUnsignedShort().toInt() }, { buf, v -> buf.writeShort((v as Int)) }),
    INT({ it.readInt() }, { buf, v -> buf.writeInt((v as Int)) }),
    DOUBLE({ it.readDouble() }, { buf, v -> buf.writeDouble((v as Double)) }),
    BOOLEAN(
        { it.readBoolean() },
        { buf, v -> buf.writeBoolean(v as Boolean) }
    )
}