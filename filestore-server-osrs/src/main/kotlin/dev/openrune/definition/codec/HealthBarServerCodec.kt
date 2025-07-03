package dev.openrune.definition.codec

import dev.openrune.definition.HealthBarServerType
import dev.openrune.definition.type.HealthBarType
import dev.openrune.definition.opcode.OpcodeDefinitionCodec
import dev.openrune.definition.opcode.DefinitionOpcode
import dev.openrune.definition.opcode.OpcodeList
import dev.openrune.definition.opcode.OpcodeType


class HealthBarServerCodec(val health: Map<Int, HealthBarType>? = null) : OpcodeDefinitionCodec<HealthBarServerType>() {

    override val definitionCodec = OpcodeList<HealthBarServerType>().apply {
        add(DefinitionOpcode(1, OpcodeType.BYTE, HealthBarServerType::width))

    }

    override fun HealthBarServerType.createData() {
        if (health == null) return

        val health = health[id]?: return

        width = health.width
    }

    override fun createDefinition(): HealthBarServerType = HealthBarServerType()

}