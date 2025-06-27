package dev.openrune.server.fg.backend

import dev.openrune.definition.util.readString
import dev.openrune.definition.util.writeString
import io.netty.buffer.ByteBuf
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

enum class OpcodeType(
    val read: (ByteBuf) -> Any,
    val write: (ByteBuf, Any) -> Unit
) {
    STRING({ it.readString() }, { buf, v -> buf.writeString(v as String) }),
    BYTE({ it.readByte() }, { buf, v -> buf.writeByte((v as Number).toInt()) }),
    SHORT({ it.readShort().toInt() }, { buf, v -> buf.writeShort((v as Number).toInt()) }),
    INT({ it.readInt() }, { buf, v -> buf.writeInt((v as Number).toInt()) })
}

interface DefinitionOpcode<T> {
    val opcode: Int
    fun decode(buffer: ByteBuf, definition: T)
    fun encode(buffer: ByteBuf, definition: T)
    fun shouldEncode(definition: T): Boolean = true
}

fun <T> DefinitionOpcode(
    opcode: Int,
    decode: (ByteBuf, T) -> Unit,
    encode: (ByteBuf, T) -> Unit,
    shouldEncode: (T) -> Boolean = { true }
): DefinitionOpcode<T> = object : DefinitionOpcode<T> {
    override val opcode = opcode
    override fun decode(buffer: ByteBuf, definition: T) = decode(buffer, definition)
    override fun encode(buffer: ByteBuf, definition: T) {
        buffer.writeByte(opcode)
        encode(buffer, definition)
    }
    override fun shouldEncode(definition: T) = shouldEncode(definition)
}

// Unified constructor for property (mutable) or getter/setter
fun <T, R> DefinitionOpcode(
    opcode: Int,
    type: OpcodeType,
    getter: (T) -> R?,
    setter: (T, R) -> Unit
): DefinitionOpcode<T> = DefinitionOpcode(
    opcode,
    decode = { buf, def ->
        @Suppress("UNCHECKED_CAST")
        val value = type.read(buf) as R
        println(value)
        setter(def, value)
    },
    encode = { buf, def ->
        getter(def)?.let { type.write(buf, it) }
    },
    shouldEncode = { getter(it) != null }
)

// Overload for KMutableProperty1 (var)
fun <T, R> DefinitionOpcode(
    opcode: Int,
    type: OpcodeType,
    property: KMutableProperty1<T, R?>
): DefinitionOpcode<T> = DefinitionOpcode(
    opcode,
    type,
    getter = { property.get(it) },
    setter = { def, value -> property.set(def, value) }
)

// Overload for KProperty1 (val or var) with mutable check
fun <T, R> DefinitionOpcode(
    opcode: Int,
    type: OpcodeType,
    property: KProperty1<T, R?>
): DefinitionOpcode<T> {
    require(property is KMutableProperty1<T, R?>) {
        "Cannot decode into read-only property '${property.name}'. Use a 'var' or provide a setter manually."
    }
    return DefinitionOpcode(opcode, type, property)
}

fun <T, R> DefinitionOpcodeList(
    opcode: Int,
    type: OpcodeType,
    property: KMutableProperty1<T, List<R>?>
): DefinitionOpcode<T> = DefinitionOpcodeList(
    opcode,
    type,
    getter = { property.get(it) },
    setter = { def, value -> property.set(def, value) }
)

fun <T, R> DefinitionOpcodeList(
    opcode: Int,
    type: OpcodeType,
    getter: (T) -> List<R>?,
    setter: (T, List<R>) -> Unit
): DefinitionOpcode<T> = DefinitionOpcode(
    opcode,
    decode = { buf, def ->
        val count = buf.readUnsignedByte().toInt()
        val list = buildList(count) {
            repeat(count) {
                @Suppress("UNCHECKED_CAST")
                add(type.read(buf) as R)
            }
        }
        setter(def, list)
    },
    encode = { buf, def ->
        val list = getter(def)
        buf.writeByte(list?.size ?: 0)
        list?.forEach { type.write(buf, it as Any) }
    },
    shouldEncode = { getter(it)?.isNotEmpty() == true }
)

fun <T, R> DefinitionOpcodeList(
    opcode: Int,
    type: OpcodeType,
    property: KProperty1<T, List<R>?>,
    customSetter: ((T, List<R>) -> Unit)? = null
): DefinitionOpcode<T> {
    if (property is KMutableProperty1<T, List<R>?>) {
        return DefinitionOpcodeList(opcode, type, property)
    }

    requireNotNull(customSetter) {
        "Cannot decode into read-only property '${property.name}'. Provide a 'var' or a custom setter."
    }

    return DefinitionOpcodeList(
        opcode,
        type,
        getter = { property.get(it) },
        setter = customSetter
    )
}