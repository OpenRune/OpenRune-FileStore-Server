package dev.openrune.server.fg.backend

class OpcodeList<T> {
    private val _opcodes = mutableListOf<DefinitionOpcode<T>>()
    val allOpcodes: List<DefinitionOpcode<T>> get() = _opcodes

    fun add(opcode: DefinitionOpcode<T>) {
        if (_opcodes.any { it.opcode == opcode.opcode }) {
            error("Opcode ${opcode.opcode} already exists in list!")
        }
        _opcodes.add(opcode)
    }

    fun addAll(vararg newOpcodes: DefinitionOpcode<T>) {
        for (opcode in newOpcodes) {
            add(opcode)
        }
    }
}