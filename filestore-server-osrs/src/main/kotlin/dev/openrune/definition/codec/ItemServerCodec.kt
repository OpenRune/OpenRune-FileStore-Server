package dev.openrune.definition.codec

import dev.openrune.definition.ItemServerType
import dev.openrune.definition.type.ItemType
import dev.openrune.server.definition.codec.CacheDefinitionCodec
import dev.openrune.server.definition.codec.opcode.DefinitionOpcode
import dev.openrune.server.definition.codec.opcode.OpcodeList
import dev.openrune.server.definition.codec.opcode.OpcodeType
import dev.openrune.server.definition.codec.opcode.impl.DefinitionOpcodeListActions
import dev.openrune.server.definition.codec.opcode.impl.DefinitionOpcodeParams


class ItemServerCodec(val items: Map<Int, ItemType>? = null) : CacheDefinitionCodec<ItemServerType>() {

    override val definitionCodec = OpcodeList<ItemServerType>().apply {
        add(DefinitionOpcode(2, OpcodeType.INT, ItemServerType::cost))
        add(DefinitionOpcode(4, OpcodeType.STRING, ItemServerType::name))
        add(DefinitionOpcode(7, OpcodeType.DOUBLE, ItemServerType::weight))
        add(DefinitionOpcode(8, OpcodeType.BOOLEAN, ItemServerType::isTradeable))
        add(DefinitionOpcode(9, OpcodeType.INT, ItemServerType::category))
        add(DefinitionOpcodeListActions(10, OpcodeType.STRING, ItemServerType::options, 5))
        add(DefinitionOpcodeListActions(11, OpcodeType.STRING, ItemServerType::interfaceOptions, 5))
        add(DefinitionOpcode(12, OpcodeType.INT, ItemServerType::noteLinkId))
        add(DefinitionOpcode(13, OpcodeType.INT, ItemServerType::noteTemplateId))
        add(DefinitionOpcode(14, OpcodeType.INT, ItemServerType::placeholderLink))
        add(DefinitionOpcode(15, OpcodeType.INT, ItemServerType::placeholderTemplate))
        add(DefinitionOpcode(16, OpcodeType.INT, ItemServerType::stacks))
        add(DefinitionOpcode(17, OpcodeType.INT, ItemServerType::equipSlot))
        add(DefinitionOpcode(18, OpcodeType.INT, ItemServerType::appearanceOverride1))
        add(DefinitionOpcode(19, OpcodeType.INT, ItemServerType::appearanceOverride2))
        add(DefinitionOpcodeParams(20, ItemServerType::params))

    }

    override fun ItemServerType.createData() {
        if (items == null) return
        val item = items[id] ?: return

        id = item.id
        cost = item.cost
        name = item.name
        weight = item.weight
        isTradeable = item.isTradeable
        category = item.category
        options = item.options
        interfaceOptions = item.interfaceOptions
        noteLinkId = item.noteLinkId
        noteTemplateId = item.noteTemplateId
        placeholderLink = item.placeholderLink
        placeholderTemplate = item.placeholderTemplate
        stacks = item.stacks
        equipSlot = item.equipSlot
        appearanceOverride1 = item.appearanceOverride1
        appearanceOverride2 = item.appearanceOverride2
        params = item.params
    }

    override fun createDefinition(): ItemServerType = ItemServerType()

}