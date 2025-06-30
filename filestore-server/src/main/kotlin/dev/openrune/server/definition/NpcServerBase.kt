package dev.openrune.server.definition

import dev.openrune.definition.Definition

abstract class NpcServerBase : Definition {
    override var id: Int = 0
    open var name : String = ""
    open var size: Int = 1
    open var category: Int = -1
    open var standAnim: Int = -1
    open var rotateLeftAnim: Int = -1
    open var rotateRightAnim: Int = -1
    open var walkAnim: Int = -1
    open var rotateBackAnim: Int = -1
    open var walkLeftAnim: Int = -1
    open var walkRightAnim: Int = -1
    open var actions: MutableList<String?> = mutableListOf(null, null, null, null, null)
    open var varbit: Int = -1
    open var varp: Int = -1
    open var transforms: MutableList<Int>? = null
    open var combatLevel: Int = -1
    open var hasRenderPriority: Boolean = false
    open var lowPriorityFollowerOps: Boolean = false
    open var isFollower: Boolean = false
    open var runSequence: Int = -1
    open var isInteractable : Boolean = true
    open var runBackSequence: Int = -1
    open var runRightSequence: Int = -1
    open var runLeftSequence: Int = -1
    open var crawlSequence: Int = -1
    open var crawlBackSequence: Int = -1
    open var crawlRightSequence: Int = -1
    open var crawlLeftSequence: Int = -1
    open var params: MutableMap<String, Any>? = null
    open var height: Int = -1
    open var attack : Int = 1
    open var defence : Int = 1
    open var strength : Int = 1
    open var hitpoints : Int = 1
    open var ranged : Int = 1
    open var magic : Int = 1
}