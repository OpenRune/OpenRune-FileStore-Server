package dev.openrune.dev.openrune.wiki.dumpers.impl

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import dev.openrune.dev.openrune.wiki.EncodingSettings
import dev.openrune.dev.openrune.wiki.Wiki
import dev.openrune.dev.openrune.wiki.dumpers.Dumper
import dev.openrune.dev.openrune.wiki.dumpers.extractIds
import dev.openrune.dev.openrune.wiki.dumpers.extractValueField
import java.lang.reflect.Type
import java.nio.file.Path

data class InfoBoxItem(
    var linkedIds: List<Int>? = null,
    val examine: String,
    val cost: Int = -1,
    val destroy: String,
    val alchable: Boolean = true
) {
    override fun hashCode(): Int {
        return (examine.hashCode() * 31 + cost.hashCode()) * 31 +
                destroy.hashCode() * 31 +
                alchable.hashCode()
    }

    companion object {
        fun load(items: Path): Map<Int, InfoBoxItem> {
            val type = object : TypeToken<Map<Int, InfoBoxItem>>() {}.type
            val original = Gson().fromJson<Map<Int, InfoBoxItem>>(items.toFile().readText(), type)

            val flatMap = mutableMapOf<Int, InfoBoxItem>()

            for ((id, item) in original) {
                flatMap[id] = item
                if (!item.linkedIds.isNullOrEmpty()) {
                    for (linkedId in item.linkedIds!!) {
                        flatMap[linkedId] = item
                    }
                }
            }

            return flatMap
        }
    }

}

class InfoBoxItemSerializer : JsonSerializer<InfoBoxItem> {
    override fun serialize(src: InfoBoxItem, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()

        // Only add fields if they are not default values
        if (!src.linkedIds.isNullOrEmpty()) {
            jsonObject.add("linkedIds", context.serialize(src.linkedIds))
        }
        if (src.examine.isNotEmpty()) {
            jsonObject.add("examine", context.serialize(src.examine))
        }
        if (src.cost != -1) {
            jsonObject.add("cost", context.serialize(src.cost))
        }
        if (src.destroy.isNotEmpty()) {
            jsonObject.add("destroy", context.serialize(src.destroy))
        }
        if (!src.alchable) {  // Only add if it's not the default value (true)
            jsonObject.add("alchable", context.serialize(src.alchable))
        }

        return jsonObject
    }
}


class Items : Dumper {

    override fun name() = "items"
    var items = mutableMapOf<Int, InfoBoxItem>()

    override fun parseItem(wiki: Wiki) {
        val parsedItems = mutableMapOf<Int, InfoBoxItem>()

        wiki.pages
            .asSequence()
            .filter { it.namespace.key == 0 }
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
                    val cost = extractValueField("value", template, keyIndex)?.toIntOrNull() ?: -1
                    val destroy = extractValueField("destroy", template, keyIndex).orEmpty()

                    var alchable = true
                    if ("alchable" in template) {
                        alchable = extractValueField("alchable", template, keyIndex) == "Yes"
                    }
                    if (cost == 0) alchable = false

                    val newItem = InfoBoxItem(emptyList(), examine, cost, destroy, alchable)
                    val existing = parsedItems.entries.find { it.value.hashCode() == newItem.hashCode() }

                    if (existing != null) {
                        val existingItem = existing.value
                        if (existing.key != id && id !in (existingItem.linkedIds ?: emptyList())) {
                            parsedItems[existing.key] = existingItem.copy(
                                linkedIds = (existingItem.linkedIds ?: emptyList()) + id
                            )
                        }
                    } else {
                        parsedItems[id] = newItem
                    }
                }
            }

        this.items = parsedItems
    }

    override fun toWrite(encodingSettings: EncodingSettings): Any {
        if (!encodingSettings.linkedIds) {
            val updates = mutableMapOf<Int, InfoBoxItem>()

            items.forEach { (id, item) ->
                val linkedIds = item.linkedIds.orEmpty()
                if (linkedIds.isNotEmpty()) {
                    val clearedItem = item.copy(linkedIds = emptyList())
                    linkedIds.forEach { linkedId -> updates[linkedId] = clearedItem }
                }
            }

            updates.forEach { (id, copiedItem) ->
                items[id] = copiedItem
            }
        }

        return items.toSortedMap()
    }

}