package dev.openrune.definition

import dev.openrune.ServerCacheManager
import dev.openrune.server.definition.ItemServerBase
import dev.openrune.server.impl.item.WeaponTypeRenderData
import dev.openrune.server.impl.item.WeaponTypes

data class ItemServerType(
    override var id: Int = -1,
    override var cost: Int = -1,
    override var name: String = "",
    override var weight: Double = 0.0,
    override var isTradeable: Boolean = false,
    override var category: Int = -1,
    override var options: MutableList<String?> = mutableListOf(null, null, "Take", null, null),
    override var interfaceOptions: MutableList<String?> = mutableListOf(null, null, null, null, "Drop"),
    override var noteLinkId: Int = -1,
    override var noteTemplateId: Int = -1,
    override var placeholderLink: Int = -1,
    override var placeholderTemplate: Int = -1,
    override var stacks: Int = 0,
    override var equipSlot: Int = 0,
    override var appearanceOverride1: Int = -1,
    override var appearanceOverride2: Int = -1,
    override var params: MutableMap<String, Any>? = null
) : ItemServerBase() {

    var equipment: Equipment? = null
    var weapon: Weapon? = null

    //TODO ADD DATA CLASS SUPPORT SO I CAN PACK THGIS NICE TO A DATACLASS :)
    fun build() {
        if (equipSlot != -1) {
            val requirements = getItemRequirements(params).takeIf { it.isNotEmpty() }

            equipment = Equipment(
                slot = equipSlot,
                requirements = requirements?: emptyMap(),
                params?: emptyMap()
            )

            if (equipSlot == 3 || equipSlot == 5) {

                val specEnum = ServerCacheManager.getEnum(906)
                val specAmount = specEnum?.values?.takeIf { it.containsKey(id.toString()) }?.getInt(id) ?: -1

                val attackSpeed = params?.getInt(14)?: 4
                val attackRange = params?.getInt(13) ?: 0

                weapon = Weapon(
                    attackSpeed = attackSpeed,
                    attackRange = attackRange,
                    specAmount = specAmount
                )
            }
        }
    }

    private val skillParamPairs = listOf(
        434 to 436,
        435 to 437,
        191 to 613,
        579 to 614,
        610 to 615,
        611 to 616,
        612 to 617,
    )

    private fun getItemRequirements(params: Map<String, Any>?): Map<String, Int> {
        if (params == null) return emptyMap()

        val enum81 = ServerCacheManager.getEnum(81) ?: return emptyMap()
        val enum108 = ServerCacheManager.getEnum(108) ?: return emptyMap()

        return skillParamPairs.mapNotNull { (skillKey, levelKey) ->
            val skillId = params[skillKey.toString()] ?: return@mapNotNull null
            val skillNameId = enum81.values[skillId] ?: return@mapNotNull null
            val skillName = enum108.values[skillNameId]?.toString()?.lowercase() ?: return@mapNotNull null
            skillName to params.getInt(levelKey)
        }.toMap()
    }

}



data class EquipmentStats(
    val attackStab: Int = 0,
    val attackSlash: Int = 0,
    val attackCrush: Int = 0,
    val attackMagic: Int = 0,
    val attackRanged: Int = 0,
    val defenceStab: Int = 0,
    val defenceSlash: Int = 0,
    val defenceCrush: Int = 0,
    val defenceMagic: Int = 0,
    val defenceRanged: Int = 0,
    val meleeStrength: Int = 0,
    val prayer: Int = 0,
    val rangedStrength: Int = 0,
    val magicStrength: Int = 0,
    val rangedDamage: Int = 0,
    val magicDamage: Int = 0,
    val demonDamage: Int = 0,
    val degradeable: Int = 0,
    val silverStrength: Int = 0,
    val corpBoost: Int = 0,
    val golemDamage: Int = 0,
    val kalphiteDamage: Int = 0
)


data class Equipment(
    val slot: Int = -1,
    val requirements: Map<String, Int> = emptyMap(),
    private val cachedParams: Map<String, Any> = emptyMap()
) {

    val stats: EquipmentStats by lazy {
        EquipmentStats(
            attackStab = cachedParams.getInt(0),
            attackSlash = cachedParams.getInt(1),
            attackCrush = cachedParams.getInt(2),
            attackMagic = cachedParams.getInt(3),
            attackRanged = cachedParams.getInt(4),
            defenceStab = cachedParams.getInt(5),
            defenceSlash = cachedParams.getInt(6),
            defenceCrush = cachedParams.getInt(7),
            defenceMagic = cachedParams.getInt(8),
            defenceRanged = cachedParams.getInt(9),
            meleeStrength = cachedParams.getInt(10),
            prayer = cachedParams.getInt(11),
            rangedStrength = cachedParams.getInt(12),
            magicStrength = cachedParams.getInt(299),
            rangedDamage = cachedParams.getInt(189),
            magicDamage = cachedParams.getInt(65),
            demonDamage = cachedParams.getInt(128),
            degradeable = cachedParams.getInt(346),
            silverStrength = cachedParams.getInt(518),
            corpBoost = cachedParams.getInt(701),
            golemDamage = cachedParams.getInt(1178),
            kalphiteDamage = cachedParams.getInt(1353)
        )
    }

    val equipmentOptions: List<String?> by lazy {
        (0 until 7).map { cachedParams.getString(451 + it).takeIf { it.isNotEmpty() } }
    }
}


data class Weapon(
    val attackSpeed: Int = 0,
    val attackRange: Int = 0,
    val specAmount: Int = -1
) {
    fun hasSpec() = specAmount != -1
}


private fun Map<String, Any?>.getString(key: Int): String =
    this[key.toString()]?.toString() ?: ""

private fun Map<String, Any?>.getInt(key: Int): Int = when (val v = this[key.toString()]) {
    is Int -> v
    is Number -> v.toInt()
    is String -> v.toIntOrNull() ?: 0
    else -> 0
}

