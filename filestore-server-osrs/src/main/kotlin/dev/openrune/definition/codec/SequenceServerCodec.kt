package dev.openrune.definition.codec

import dev.openrune.definition.SequenceServerType
import dev.openrune.definition.type.SequenceType
import dev.openrune.server.definition.codec.CacheDefinitionCodec
import dev.openrune.server.definition.codec.opcode.DefinitionOpcode
import dev.openrune.server.definition.codec.opcode.OpcodeList
import dev.openrune.server.definition.codec.opcode.OpcodeType


class SequenceServerCodec(val sequences: Map<Int, SequenceType>? = null) : CacheDefinitionCodec<SequenceServerType>() {

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