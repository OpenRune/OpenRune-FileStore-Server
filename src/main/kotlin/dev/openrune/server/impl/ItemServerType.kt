package dev.openrune.server.impl

import dev.openrune.definition.type.ItemType
import dev.openrune.dev.openrune.wiki.dumpers.impl.InfoBoxItem

data class ItemServerType(
    var id : Int = -1,
    var examine : String = "",
    var cost : Int = -1,
    var name : String = "",
    var destroy : String = "",
    var alchable : Boolean = true
) {
   companion object {
       fun load(id : Int,infoBoxItem: InfoBoxItem?,cache : ItemType): ItemServerType {
           val type = ItemServerType().apply {
               this.id = id
               name = cache.name
               cost = infoBoxItem?.cost?.takeIf { it != -1 } ?: cache.cost
               examine = infoBoxItem?.examine?.takeIf { it.isNotEmpty() } ?: cache.examine
               destroy = infoBoxItem?.destroy?.takeIf { it.isNotEmpty() } ?: ""
               alchable = infoBoxItem?.alchable ?: true
           }
           return type
       }
   }
    

}