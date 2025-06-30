package dev.openrune.server.definition.codec.opcode

import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

// Unified getter/setter helpers
fun <T, R> KMutableProperty1<T, R?>.toGetterSetter(): Pair<(T) -> R?, (T, R) -> Unit> =
    ({ receiver: T -> this.get(receiver) }) to ({ receiver: T, value: R -> this.set(receiver, value) })

fun <T, R> KProperty1<T, R?>.toGetterSetter(customSetter: ((T, R) -> Unit)? = null): Pair<(T) -> R?, (T, R) -> Unit> {
    if (this is KMutableProperty1<T, R?>) {
        return this.toGetterSetter()
    }
    requireNotNull(customSetter) { "Cannot decode into read-only property '${this.name}'. Provide a setter." }
    return ({ receiver: T -> this.get(receiver) }) to customSetter
}

fun <T, R> DefinitionOpcode(
    opcode: Int,
    type: OpcodeType,
    getter: (T) -> R?,
    setter: (T, R) -> Unit
): DefinitionOpcode<T> = DefinitionOpcode(
    opcode,
    decode = { buf, def, _ ->
        @Suppress("UNCHECKED_CAST")
        val value = type.read(buf) as R
        setter(def, value)
    },
    encode = { buf, def ->
        getter(def)?.let { type.write(buf, it) }
    },
    shouldEncode = { getter(it) != null }
)

fun <T, R> DefinitionOpcode(
    opcode: Int,
    type: OpcodeType,
    property: KMutableProperty1<T, R?>
): DefinitionOpcode<T> {
    val (getter, setter) = property.toGetterSetter()
    return DefinitionOpcode(opcode, type, getter, setter)
}

fun <T, R> DefinitionOpcode(
    opcode: Int,
    type: OpcodeType,
    property: KProperty1<T, R?>,
    customSetter: ((T, R) -> Unit)? = null
): DefinitionOpcode<T> {
    val (getter, setter) = property.toGetterSetter(customSetter)
    return DefinitionOpcode(opcode, type, getter, setter)
}