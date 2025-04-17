package dev.openrune.wiki.dumpers.impl

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import dev.openrune.dev.openrune.wiki.EncodingSettings
import dev.openrune.dev.openrune.wiki.Wiki
import dev.openrune.wiki.dumpers.Dumper
import io.github.oshai.kotlinlogging.KotlinLogging
import java.net.URL
import java.nio.file.Path
import kotlin.collections.set

data class InfoBoxObject(
    var linkedIds: List<Int>? = null,
    val examine: String
) {
    override fun hashCode(): Int {
        return (examine.hashCode())
    }

    companion object {
        fun load(items: Path): Map<Int, InfoBoxObject> {
            val type = object : TypeToken<Map<Int, InfoBoxObject>>() {}.type
            val original = Gson().fromJson<Map<Int, InfoBoxObject>>(items.toFile().readText(), type)

            val flatMap = mutableMapOf<Int, InfoBoxObject>()

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


class Objects : Dumper {

    val objsLink = "https://raw.githubusercontent.com/Joshua-F/osrs-examines/refs/heads/master/objs.csv"

    private val logger = KotlinLogging.logger {}

    override fun name() = "object"
    var objects = mutableMapOf<Int, InfoBoxObject>()

    override fun parseItem(wiki: Wiki) {
        val parsedObject = mutableMapOf<Int, InfoBoxObject>()
        val data = URL(objsLink).readText()
        data.lines().forEach {
            val info = it.split(",", limit = 2)
            val id = info[0].toIntOrNull()
            val secondPart = info.getOrNull(1)
            if (id != null && secondPart != null) {
                val newObject = InfoBoxObject(emptyList(),secondPart)

                val existing = parsedObject.entries.find { it.value.hashCode() == newObject.hashCode() }

                if (existing != null) {
                    val existingItem = existing.value
                    if (existing.key != id && id !in (existingItem.linkedIds ?: emptyList())) {
                        parsedObject[existing.key] = existingItem.copy(
                            linkedIds = (existingItem.linkedIds ?: emptyList()) + id
                        )
                    }
                } else {
                    parsedObject[id] = newObject
                }
            }
        }

        this.objects = parsedObject
    }

    override fun toWrite(encodingSettings: EncodingSettings): Any {
        if (!encodingSettings.linkedIds) {
            val updates = mutableMapOf<Int, InfoBoxObject>()

            objects.forEach { (id, item) ->
                val linkedIds = item.linkedIds.orEmpty()
                if (linkedIds.isNotEmpty()) {
                    val clearedItem = item.copy(linkedIds = emptyList())
                    linkedIds.forEach { linkedId -> updates[linkedId] = clearedItem }
                }
            }

            updates.forEach { (id, copiedItem) ->
                objects[id] = copiedItem
            }
        }

        return objects.toSortedMap()
    }

}