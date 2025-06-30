package dev.openrune.wiki.infobox

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
    val attributes: List<MonsterAttribute>,
    val immunepoison : Boolean = false,
    val immunevenom : Boolean = false,
    val immunecannon : Boolean = false,
    val immunethrall : Boolean = false,
    val aggressive : Boolean = false,
    val attackSpeed : Int = 4,
    val poisonousLevel  : Int = -1,
    val respawn : Int = -1,
    val slayerXp : Int = 1,
    val slayerMasters : List<SlayerMaster> = emptyList(),
    val elementalTypes : Map<ElementalType,Int> = emptyMap(),
    val attack : Int = 0,
    val strength : Int = 0,
    val magic : Int = 0,
    val magicstrength : Int = 0,
    val rangeddefence : Int = 0,
    val rangestrength : Int = 0,
    val stabdefence : Int = 0,
    val slashdefence : Int = 0,
    val crushdefence : Int = 0,
    val magicdefence : Int = 0,
    val lightdefence : Int = 0,
    val standardefence : Int = 0,
    val heavydefence : Int = 0,
) {
    override fun hashCode(): Int {
        return (examine.hashCode() * 31)
    }

    companion object {
        fun load(items: Path): Map<Int, InfoBoxNpc> {
            val type = object : TypeToken<Map<Int, InfoBoxNpc>>() {}.type
            val original = Gson().fromJson<Map<Int, InfoBoxNpc>>(items.toFile().readText(), type)

            val flatMap = mutableMapOf<Int, InfoBoxNpc>()

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