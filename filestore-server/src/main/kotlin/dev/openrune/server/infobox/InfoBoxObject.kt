package dev.openrune.server.infobox

import com.google.gson.*
import com.google.gson.reflect.TypeToken
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