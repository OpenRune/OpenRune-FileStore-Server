package dev.openrune.server.impl

import dev.openrune.definition.Definition
import dev.openrune.definition.type.NpcType
import dev.openrune.wiki.dumpers.impl.InfoBoxItem
import dev.openrune.wiki.dumpers.impl.InfoBoxObject

data class NpcServerType(
    override var id: Int = -1,
    var examine: String = "",
    var size : Int = 1,
    var category : Int = -1,
    var standAnim : Int = -1,
    var rotateLeftAnim : Int = -1,
    var rotateRightAnim : Int = -1,
    var walkAnim : Int = -1,
    var rotateBackAnim : Int = -1,
    var walkLeftAnim : Int = -1,
    var walkRightAnim : Int = -1,
    var actions : MutableList<String?> = mutableListOf(null, null, null, null, null),
    var varbit: Int = -1,
    var varp: Int = -1,
    var transforms: MutableList<Int>? = null,
    var combatLevel : Int = -1,
    var hasRenderPriority : Boolean = false,
    var lowPriorityFollowerOps : Boolean = false,
    var isFollower : Boolean = false,
    var runSequence : Int = -1,
    var runBackSequence : Int = -1,
    var runRightSequence : Int = -1,
    var runLeftSequence : Int = -1,
    var crawlSequence : Int = -1,
    var crawlBackSequence : Int = -1,
    var crawlRightSequence : Int = -1,
    var crawlLeftSequence : Int = -1,
    var params: MutableMap<String, Any>? = null,
) : Definition {

    companion object {
        fun load(id: Int, infoBoxObject: InfoBoxItem?, cache : NpcType): NpcServerType {

            return NpcServerType().apply {
                this.id = id
                examine = cache.examine
                size = cache.size
                category = cache.category
                standAnim = cache.standAnim
                rotateLeftAnim = cache.rotateLeftAnim
                rotateRightAnim = cache.rotateRightAnim
                walkAnim = cache.walkAnim
                rotateBackAnim = cache.rotateBackAnim
                walkLeftAnim = cache.walkLeftAnim
                walkRightAnim = cache.walkRightAnim
                actions = cache.actions
                varbit = cache.varbit
                varp = cache.varp
                transforms = cache.transforms
                combatLevel = cache.combatLevel
                lowPriorityFollowerOps = cache.lowPriorityFollowerOps
                isFollower = cache.isFollower
                runSequence = cache.runSequence
                runBackSequence = cache.runBackSequence
                runRightSequence = cache.runRightSequence
                runLeftSequence = cache.runLeftSequence
                crawlSequence = cache.crawlSequence
                crawlBackSequence = cache.crawlBackSequence
                crawlRightSequence = cache.crawlRightSequence
                crawlLeftSequence = cache.crawlLeftSequence
                params = cache.params

            }
        }
    }

}

