package dev.openrune.definition.codec

import dev.openrune.definition.ObjectServerType
import dev.openrune.definition.type.ObjectType
import dev.openrune.server.definition.codec.CacheDefinitionCodec
import dev.openrune.server.definition.codec.opcode.DefinitionOpcode
import dev.openrune.server.definition.codec.opcode.OpcodeList
import dev.openrune.server.definition.codec.opcode.OpcodeType
import dev.openrune.server.definition.codec.opcode.impl.DefinitionOpcodeListActions
import dev.openrune.server.definition.codec.opcode.impl.DefinitionOpcodeParams
import dev.openrune.server.definition.codec.opcode.impl.DefinitionOpcodeTransforms


class ObjectServerCodec(val objects: Map<Int, ObjectType>? = null) : CacheDefinitionCodec<ObjectServerType>() {

    override val definitionCodec = OpcodeList<ObjectServerType>().apply {
        add(DefinitionOpcode(1, OpcodeType.STRING, ObjectServerType::name))
        add(DefinitionOpcode(3, OpcodeType.BYTE, ObjectServerType::sizeX))
        add(DefinitionOpcode(4, OpcodeType.BYTE, ObjectServerType::sizeY))
        add(DefinitionOpcode(5, OpcodeType.USHORT, ObjectServerType::offsetX))
        add(DefinitionOpcode(6, OpcodeType.BYTE, ObjectServerType::interactive))
        add(DefinitionOpcode(7, OpcodeType.INT, ObjectServerType::solid))
        add(DefinitionOpcode(9, OpcodeType.SHORT, ObjectServerType::modelSizeX))
        add(DefinitionOpcode(10, OpcodeType.SHORT, ObjectServerType::modelSizeZ))
        add(DefinitionOpcode(11, OpcodeType.SHORT, ObjectServerType::modelSizeY))
        add(DefinitionOpcode(12, OpcodeType.SHORT, ObjectServerType::offsetZ))
        add(DefinitionOpcode(13, OpcodeType.SHORT, ObjectServerType::offsetY))
        add(DefinitionOpcode(14, OpcodeType.BYTE, ObjectServerType::clipMask))
        add(DefinitionOpcode(15, OpcodeType.BOOLEAN, ObjectServerType::obstructive))
        add(DefinitionOpcode(16, OpcodeType.USHORT, ObjectServerType::category))
        add(DefinitionOpcode(17, OpcodeType.BYTE, ObjectServerType::supportsItems))
        add(DefinitionOpcode(18, OpcodeType.BOOLEAN, ObjectServerType::isRotated))
        add(DefinitionOpcode(19, OpcodeType.BOOLEAN, ObjectServerType::impenetrable))
        add(DefinitionOpcode(20, OpcodeType.USHORT, ObjectServerType::replacementId))
        add(DefinitionOpcodeTransforms(IntRange(23, 24),ObjectServerType::transforms,ObjectServerType::varbit,ObjectServerType::varp))
        add(DefinitionOpcodeListActions(25, OpcodeType.STRING, ObjectServerType::actions, 5))
        add(DefinitionOpcodeParams(26, ObjectServerType::params))

    }

    override fun ObjectServerType.createData() {
        if (objects == null) return

        val obj = objects[id]?: return

        name = obj.name
        sizeX = obj.sizeX
        sizeY = obj.sizeY
        offsetX = obj.offsetX
        interactive = obj.interactive
        solid = obj.solid
        actions = obj.actions
        modelSizeX = obj.modelSizeX
        modelSizeZ = obj.modelSizeZ
        modelSizeY = obj.modelSizeY
        offsetZ = obj.offsetZ
        offsetY = obj.offsetY
        clipMask = obj.clipMask
        obstructive = obj.obstructive
        category = obj.category
        supportsItems = obj.supportsItems
        isRotated = obj.isRotated
        impenetrable = obj.impenetrable
        replacementId = obj.oppositeDoorId(objects)
        varbit = obj.varbitId
        varp = obj.varp
        transforms = obj.transforms
        params = obj.params

    }

    override fun createDefinition(): ObjectServerType = ObjectServerType()

}