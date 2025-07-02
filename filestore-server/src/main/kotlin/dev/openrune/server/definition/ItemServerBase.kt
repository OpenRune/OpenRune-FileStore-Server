package dev.openrune.server.definition

import dev.openrune.definition.Definition

abstract class ItemServerBase : Definition {
    override var id: Int = 0
    open var cost: Int = -1
    open var name: String = ""
    open var weight: Double = 0.0
    open var isTradeable: Boolean = false
    open var category: Int = -1
    open var options: MutableList<String?> = mutableListOf(null, null, "Take", null, null)
    open var interfaceOptions: MutableList<String?> = mutableListOf(null, null, null, null, "Drop")
    open var noteLinkId: Int = -1
    open var noteTemplateId: Int = -1
    open var placeholderLink: Int = -1
    open var placeholderTemplate: Int = -1
    open var stacks: Int = 0
    open var equipSlot: Int = -1
    open var appearanceOverride1: Int = -1
    open var appearanceOverride2: Int = -1
    open var params: MutableMap<String, Any>? = null
    val slot: Int = -1

    val stackable: Boolean
        get() = stacks == 1 || noteTemplateId > 0

    val noted: Boolean
        get() = noteTemplateId > 0

    /**
     * Whether or not the object is a placeholder.
     */
    val isPlaceholder
        get() = placeholderTemplate > 0 && placeholderLink > 0


}
