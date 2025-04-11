package dev.openrune.dev.openrune.wiki.dumpers.impl

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import dev.openrune.dev.openrune.wiki.EncodingSettings
import dev.openrune.dev.openrune.wiki.Wiki
import dev.openrune.dev.openrune.wiki.dumpers.Dumper
import dev.openrune.dev.openrune.wiki.dumpers.extractIds
import dev.openrune.dev.openrune.wiki.dumpers.extractValueField
import java.lang.reflect.Type

data class InfoBoxItem(
    var linkedIds : List<Int> = emptyList(),
    val examine : String,
    val cost : Int,
    val destroy : String,
    val alchable : Boolean = true
) {
    override fun hashCode(): Int {
        return (examine.hashCode() * 31 + cost.hashCode()) * 31 +
                destroy.hashCode() * 31 +
                alchable.hashCode()
    }
}

class InfoBoxItemSerializer : JsonSerializer<InfoBoxItem> {
    override fun serialize(src: InfoBoxItem, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()

        // Only add fields if they are not default values
        if (src.linkedIds.isNotEmpty()) {
            jsonObject.add("linkedIds", context.serialize(src.linkedIds))
        }
        if (src.examine.isNotEmpty()) {
            jsonObject.add("examine", context.serialize(src.examine))
        }
        if (src.cost != 0) {
            jsonObject.add("cost", context.serialize(src.cost))
        }
        if (src.destroy.isNotEmpty()) {
            jsonObject.add("destroy", context.serialize(src.destroy))
        }
        if (src.alchable != true) {  // Only add if it's not the default value (true)
            jsonObject.add("alchable", context.serialize(src.alchable))
        }

        return jsonObject
    }
}


class Items : Dumper {

    override fun name() = "items"

    var items = mutableMapOf<Int, InfoBoxItem>()

    override fun parseItem(wiki: Wiki) {
        val items = mutableMapOf<Int, InfoBoxItem>()

        wiki.pages
            .asSequence()
            .filter { it.namespace.key == 0 } // Mainspace only
            .filter { it.revision.text.contains("infobox item", ignoreCase = true) }
            .forEach { page ->

                val templates = page.getTemplateMaps("infobox item")
                if (templates.isEmpty()) return@forEach

                val template = templates.first()
                val idKeys = template.keys.filter { it == "id" || it.matches(Regex("id\\d+")) }
                val idCount = idKeys.size

                val ids = mutableListOf<Pair<Int, Int>>()
                for (keyIndex in 1..idCount) {
                    extractIds(template, keyIndex, ids)
                }

                ids.forEach { (id, keyIndex) ->
                    val examine = extractValueField("examine", template, keyIndex).orEmpty()
                    val cost = extractValueField("value", template, keyIndex)?.toIntOrNull() ?: 0
                    val destroy = extractValueField("destroy", template, keyIndex).orEmpty()
                    var alchable = true

                    if (template.contains("alchable")) {
                        alchable = extractValueField("alchable", template, keyIndex) == "Yes"
                    }

                    if (cost == 0) {
                        alchable = false
                    }



                    val newItem = InfoBoxItem(emptyList(), examine, cost, destroy, alchable)
                    val existingEntry = items.entries.find { it.value.hashCode() == newItem.hashCode() }

                    if (existingEntry != null) {
                        val existingItem = existingEntry.value
                        if (existingEntry.key != id && id !in existingItem.linkedIds) {
                            items[existingEntry.key] = existingItem.copy(linkedIds = existingItem.linkedIds + id)
                        }
                    } else {
                        items[id] = newItem
                    }

                }

            }

        this.items = items
    }

    override fun toWrite(encodingSettings : EncodingSettings): Any {

        if (!encodingSettings.linkedIds) {
            val updates = mutableMapOf<Int, InfoBoxItem>()
            items.forEach { entry ->
                val linkedIds = entry.value.linkedIds
                if (linkedIds.isNotEmpty()) {
                    entry.value.linkedIds = emptyList()
                    val copy = entry.value.copy()
                    linkedIds.forEach { updates[it] = copy }
                }
            }

            updates.forEach { (id, copiedItem) -> items[id] = copiedItem }
        }

        return items.toList().sortedBy { it.first }.toMap().toMutableMap()
    }
}