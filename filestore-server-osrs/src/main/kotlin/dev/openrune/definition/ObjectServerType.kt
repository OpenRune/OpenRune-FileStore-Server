package dev.openrune.definition

import dev.openrune.server.definition.ObjectServerBase

data class ObjectServerType(
    override var id: Int = -1,
    override var name : String = "",
    override var sizeX: Int = 1,
    override var sizeY: Int = 1,
    override var offsetX: Int = 0,
    override var interactive: Int = -1,
    override var solid: Int = 2,
    override var actions: MutableList<String?> = mutableListOf(null, null, null, null, null),
    override var modelSizeX: Int = 128,
    override var modelSizeZ: Int = 128,
    override var modelSizeY: Int = 128,
    override var offsetZ: Int = 0,
    override var offsetY: Int = 0,
    override var clipMask: Int = 0,
    override var obstructive: Boolean = false,
    override var category: Int = -1,
    override var supportsItems: Int = -1,
    override var isRotated: Boolean = false,
    override  var impenetrable: Boolean = true,
    override var replacementId: Int = -1,
    override var varbit: Int = -1,
    override var varp: Int = -1,
    override var transforms: MutableList<Int>? = null,
    override var params: MutableMap<String, Any>? = null
) : ObjectServerBase()