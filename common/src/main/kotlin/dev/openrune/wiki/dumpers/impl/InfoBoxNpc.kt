package dev.openrune.wiki.dumpers.impl

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.nio.file.Path

enum class ElementalType {
    FIRE,
    EARTH,
    WATER,
    AIR
}

enum class SlayerMaster {
    TURAEL,
    AYA,
    SPRIA,
    KRYSTILIA,
    MAZCHNA,
    ACHTTRYN,
    VANNAKA,
    CHAELDAR,
    KONAR,
    NIEVE,
    STEVE,
    DURADEL,
    KURADAL
}

enum class MaxHitType {
    MELEE,
    RANGE,
    MAGIC,
    SLASH,
    DRAGONFIRE,
    ALL,
    CRUSH
}

enum class MonsterAttribute {
    DEMON,
    DRACONIC,
    FIERY,
    GOLEM,
    KALPHITE,
    LEAFY,
    PENANCE,
    RAT,
    SHADE,
    SPECTRAL,
    UNDEAD,
    VAMPIRE,
    XERICIAN
}

data class InfoBoxNpc(
    var linkedIds: List<Int>? = null,
    val examine: String,
    val maxHit: Map<MaxHitType,Int>,
    val maxHitExtra: Map<String,Int>,
    val attributes: List<MonsterAttribute>
) {
    override fun hashCode(): Int {
        return (examine.hashCode() * 31)
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