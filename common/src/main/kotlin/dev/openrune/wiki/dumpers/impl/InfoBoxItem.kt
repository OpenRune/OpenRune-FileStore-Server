package dev.openrune.wiki.dumpers.impl

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.nio.file.Path

data class InfoBoxItem(
    var linkedIds: List<Int>? = null,
    val examine: String,
    val cost: Int = -1,
    val exchangeCost: Int = -1,
    val destroy: String,
    val alchable: Boolean = true,
    val attackRange: Int = -1,
    val combatStyle: String,
    val itemReq: Map<String, Int>
) {
    override fun hashCode(): Int {
        return (examine.hashCode() * 31 + cost.hashCode()) * 31 +
                destroy.hashCode() * 31 +
                alchable.hashCode() * 31 +
                attackRange.hashCode() * 31 +
                combatStyle.hashCode() * 31 +
                itemReq.hashCode()
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