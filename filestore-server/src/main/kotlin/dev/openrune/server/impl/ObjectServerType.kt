package dev.openrune.server.impl

import dev.openrune.definition.Definition
import dev.openrune.definition.type.ObjectType
import dev.openrune.server.infobox.InfoBoxObject

data class ObjectServerType(
    override var id: Int = -1,
    var examine: String = "",
    var name : String = "",
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
    var replacementId: Int = -1,
    var varbit: Int = -1,
    var varp: Int = -1,
    var transforms: MutableList<Int>? = null,
    var params: MutableMap<String, Any>? = null
) : Definition {

    fun hasOption(vararg searchOptions: String): Boolean {
        return searchOptions.any { option ->
            actions.any { it.equals(option, ignoreCase = true) }
        }
    }

    fun getOption(vararg searchOptions: String): Int {
        searchOptions.forEach {
            actions.forEachIndexed { index, option ->
                if (it.equals(option, ignoreCase = true)) return index + 1
            }
        }
        return -1
    }

    companion object {
        fun load(id: Int, infoBoxObject: InfoBoxObject?, values: Map<Int, ObjectType?>): ObjectServerType {

            val cache = values[id]?: error("Object $id not found in cache")

            return ObjectServerType().apply {
                this.id = id
                name = cache.name
                examine = infoBoxObject?.examine?.takeIf { it.isNotEmpty() } ?: "null"
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
                replacementId = cache.oppositeDoorId(values)
                varbit = cache.varbitId
                varp = cache.varp
                transforms = cache.transforms
                params = cache.params

            }
        }
    }

}

