package dev.openrune.definition.codec

import dev.openrune.definition.SequenceServerType
import dev.openrune.definition.type.SequenceType
import dev.openrune.definition.opcode.OpcodeDefinitionCodec
import dev.openrune.definition.opcode.DefinitionOpcode
import dev.openrune.definition.opcode.OpcodeList
import dev.openrune.definition.opcode.OpcodeType


class SequenceServerCodec(val sequences: Map<Int, SequenceType>? = null) : OpcodeDefinitionCodec<SequenceServerType>() {

    override val definitionCodec = OpcodeList<SequenceServerType>().apply {
        add(DefinitionOpcode(1, OpcodeType.USHORT, SequenceServerType::animationLength))

    }

    override fun SequenceServerType.createData() {
        if (sequences == null) return

        val seq = sequences[id]?: return

        animationLength = seq.lengthInCycles
    }

    override fun createDefinition(): SequenceServerType = SequenceServerType()

}