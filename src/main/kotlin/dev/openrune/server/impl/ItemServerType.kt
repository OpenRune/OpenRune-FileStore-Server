package dev.openrune.dev.openrune.server.impl

import dev.openrune.cache.CacheManager
import dev.openrune.definition.type.ItemType

data class ItemServerType(
    val id : Int = -1,
    val examine : String = "",
    val cost : Int = 0,
    var name : String = ""
) {
    fun load() {
        name = CacheManager.getItemOrDefault(id).name
    }
}