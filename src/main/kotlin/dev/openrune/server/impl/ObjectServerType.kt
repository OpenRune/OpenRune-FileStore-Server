package dev.openrune.server.impl

import dev.openrune.definition.Definition
import dev.openrune.definition.type.ObjectType
import dev.openrune.wiki.dumpers.impl.InfoBoxObject


data class ObjectServerType(
    override var id: Int = -1,
    var examine: String = "",
    var sizeX: Int = 1,
    var sizeY: Int = 1,
    var offsetX: Int = 0,
    var interactive: Int = -1,
    var solid: Int = 2,
    var actions: MutableList<String?> = mutableListOf(null, null, null, null, null),
    var modelSizeX: Int = 128,
    var modelSizeZ: Int = 128,
    var modelSizeY: Int = 128,
    var offsetZ: Int = 0,
    var offsetY: Int = 0,
    var clipMask: Int = 0,
    var obstructive: Boolean = false,
    var category: Int = -1,
    var supportsItems: Int = -1,
    var isRotated: Boolean = false,
    var impenetrable: Boolean = true,
    var varbit: Int = -1,
    var varp: Int = -1,
    var transforms: MutableList<Int>? = null,
    var params: MutableMap<String, Any>? = null
) : Definition {
    companion object {
        fun load(id: Int, infoBoxObject: InfoBoxObject?, cache: ObjectType): ObjectServerType {
            return ObjectServerType().apply {
                this.id = id
                examine =  infoBoxObject?.examine?.takeIf { it.isNotEmpty() } ?: "null"
                sizeX = cache.sizeX
                sizeY = cache.sizeY
                offsetX = cache.offsetX
                interactive = cache.interactive
                solid = cache.solid
                actions = cache.actions
                modelSizeX = cache.modelSizeX
                modelSizeZ = cache.modelSizeZ
                modelSizeY = cache.modelSizeY
                offsetZ = cache.offsetZ
                offsetY = cache.offsetY
                clipMask = cache.clipMask
                obstructive = cache.obstructive
                category = cache.category
                supportsItems = cache.supportsItems
                isRotated = cache.isRotated
                impenetrable = cache.impenetrable
                varbit = cache.varbitId
                varp = cache.varp
                transforms = cache.transforms
                params = cache.params

            }
        }
    }

}

