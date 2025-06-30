package dev.openrune.server.definition

import dev.openrune.definition.Definition

abstract class ObjectServerBase : Definition {
    override var id: Int = 0
    open var name: String = ""
    open var sizeX: Int = 1
    open var sizeY: Int = 1
    open var offsetX: Int = 0
    open var interactive: Int = -1
    open var solid: Int = 2
    open var actions: MutableList<String?> = mutableListOf(null, null, null, null, null)
    open var modelSizeX: Int = 128
    open var modelSizeZ: Int = 128
    open var modelSizeY: Int = 128
    open var offsetZ: Int = 0
    open var offsetY: Int = 0
    open var clipMask: Int = 0
    open var obstructive: Boolean = false
    open var category: Int = -1
    open var supportsItems: Int = -1
    open var isRotated: Boolean = false
    open var impenetrable: Boolean = true
    open var replacementId: Int = -1
    open var varbit: Int = -1
    open var varp: Int = -1
    open var transforms: MutableList<Int>? = null
    open var params: MutableMap<String, Any>? = null
}