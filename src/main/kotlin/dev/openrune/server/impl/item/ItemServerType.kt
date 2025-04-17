package dev.openrune.server.impl.item

import dev.openrune.definition.Definition
import dev.openrune.definition.type.ItemType
import dev.openrune.server.ServerCacheManager
import dev.openrune.wiki.dumpers.impl.InfoBoxItem
import kotlin.collections.get

data class Equipment(
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
    val rangedStrength: Int = 0,
    val rangedEquipmentStrengthBonus: Int = 0,
    val magicStrength: Int = 0,
    val magicDamage: Int = 0,
    val prayer: Int = 0,
    val demonDamage: Int = 0,
    val degradeable: Int = 0,
    val silverStrength: Int = 0,
    val corpBoost: Int = 0,
    val golemDamage: Int = 0,
    val kalphiteDamage: Int = 0,
    val slot: Int = -1,
    var equipmentOptions: List<String?> = emptyList(),
    val requirements: Map<String, Int> = emptyMap()
)

data class Weapon(
    val attackSpeed: Int = 0,
    val weaponTypeRenderData: WeaponTypeRenderData?,
    val weaponType: WeaponTypes = WeaponTypes.UNARMED,
    val attackRange: Int = 0,
    val hasSpec: Boolean = false
)

data class ItemServerType(
    override var id: Int = -1,
    var examine: String = "",
    var cost: Int = -1,
    var exchangeCost: Int = -1,
    var name: String = "",
    var destroy: String = "",
    var alchable: Boolean = true,
    var weight: Double = 0.0,
    var isTradeable: Boolean = false,
    var category: Int = -1,
    var options: MutableList<String?> = mutableListOf(null, null, "Take", null, null),
    var interfaceOptions: MutableList<String?> = mutableListOf(null, null, null, null, "Drop"),
    var noteLinkId: Int = -1,
    var noteTemplateId: Int = -1,
    var placeholderLink: Int = -1,
    var placeholderTemplate: Int = -1,
    var stacks: Int = 0,
    var subops: Array<Array<String?>?>? = null,
    var appearanceOverride1: Int = -1,
    var appearanceOverride2: Int = -1,
    var equipment: Equipment? = null,
    var weapon: Weapon? = null,
    var params: MutableMap<Int, Any>? = null
) : Definition {
    companion object {
        fun load(id: Int, infoBoxItem: InfoBoxItem?, cache: ItemType): ItemServerType {
            return ItemServerType().apply {
                this.id = id
                name = cache.name
                cost = infoBoxItem?.cost?.takeIf { it != -1 } ?: cache.cost
                exchangeCost = infoBoxItem?.exchangeCost?.takeIf { it != -1 } ?: cost
                examine = infoBoxItem?.examine?.takeIf { it.isNotEmpty() } ?: cache.examine
                destroy = infoBoxItem?.destroy?.takeIf { it.isNotEmpty() } ?: ""
                alchable = infoBoxItem?.alchable ?: true
                weight = cache.weight
                isTradeable = cache.isTradeable
                category = cache.category
                options = cache.options
                interfaceOptions = cache.interfaceOptions
                noteLinkId = cache.noteLinkId
                noteTemplateId = cache.noteTemplateId
                placeholderLink = cache.placeholderLink
                placeholderTemplate = cache.placeholderTemplate
                stacks = cache.stacks
                subops = cache.subops
                appearanceOverride1 = cache.appearanceOverride1
                appearanceOverride2 = cache.appearanceOverride2
                params = cache.params

                if (cache.equipSlot != -1) {
                    val params = cache.params
                    equipment = Equipment(
                        attackStab = params.getInt(0),
                        attackSlash = params.getInt(1),
                        attackCrush = params.getInt(2),
                        attackMagic = params.getInt(3),
                        attackRanged = params.getInt(4),
                        defenceStab = params.getInt(5),
                        defenceSlash = params.getInt(6),
                        defenceCrush = params.getInt(7),
                        defenceMagic = params.getInt(8),
                        defenceRanged = params.getInt(9),
                        meleeStrength = params.getInt(10),
                        prayer = params.getInt(11),
                        rangedStrength = params.getInt(12),
                        magicStrength = params.getInt(299),
                        rangedEquipmentStrengthBonus = params.getInt(189),
                        magicDamage = params.getInt(65),
                        demonDamage = params.getInt(128),
                        degradeable = params.getInt(346),
                        silverStrength = params.getInt(518),
                        corpBoost = params.getInt(701),
                        golemDamage = params.getInt(1178),
                        kalphiteDamage = params.getInt(1353),
                        equipmentOptions = getEquipmentOptions(params),
                        requirements = getItemRequirements(params).takeIf { it.isNotEmpty() } ?: infoBoxItem?.itemReq?: emptyMap(),
                        slot = cache.equipSlot
                    )

                    if (cache.equipSlot == 3 || cache.equipSlot == 5) {
                        val weaponType = if (infoBoxItem?.combatStyle == null) WeaponTypes.UNARMED else WeaponTypes.valueOf(infoBoxItem.combatStyle.uppercase().replace(" ", "_"))
                        val weaponTypeRenderData = findWeaponTypeRenderData(id, weaponType)

                        weapon = Weapon(
                            attackSpeed = params.getInt(14),
                            weaponType = weaponType,
                            weaponTypeRenderData = weaponTypeRenderData,
                            attackRange = params?.takeIf { it.containsKey(13) }?.getInt(13) ?: infoBoxItem?.attackRange ?: 0,
                            hasSpec = ServerCacheManager.getEnum(906)?.values?.takeIf { it.containsKey(id) } != null
                        )
                    }
                }
            }
        }
    }

    fun findWeaponTypeRenderData(id : Int, weaponType : WeaponTypes) : WeaponTypeRenderData? {
        val renderData = ItemRenderDataManager.getItemRenderAnimationByItem(id)
        if (renderData != null) return renderData.toServer()
        //println("Missing WeaponTypeRenderData for {${id}, ${weaponType.name}} ")
        return ItemRenderDataManager.getItemRenderAnimationById(weaponType.fallbackRenderID)?.toServer()
    }

    private val skillParamPairs = listOf(
        Pair(434,436),
        Pair(435,437),
        Pair(191,613),
        Pair(579,614),
        Pair(610,615),
        Pair(611,616),
        Pair(612,617),
    )

    fun getItemRequirements(params: Map<Int, Any>?) : Map<String,Int> {
        if (params == null) return emptyMap()
        val req : MutableMap<String,Int> = emptyMap<String, Int>().toMutableMap()

        skillParamPairs.forEach {
            if (params.containsKey(it.first)) {
                val statID = ServerCacheManager.getEnum(81)!!.values[params[it.first]]
                val skill = ServerCacheManager.getEnum(108)!!.values[statID]
                req[skill.toString().lowercase()] = params.getInt(it.second)
            }
        }

        return req
    }

    fun getEquipmentOptions(params: Map<Int, Any>?): List<String?> {
        if (params == null) return MutableList<String?>(7) { null }

        val equipmentOptions = MutableList<String?>(7) { null }

        for (index in 0..6) {
            val param = params.getString(451 + index)
            if (param.isNotEmpty()) {
                equipmentOptions[index] = param
            }
        }
        return equipmentOptions
    }

    fun isEquippable() = equipment != null

    fun isWeapon() = weapon != null

    val stackable: Boolean
        get() = stacks == 1 || noteTemplateId > 0

    val noted: Boolean
        get() = noteTemplateId > 0

    val isPlaceholder: Boolean
        get() = placeholderTemplate > 0 && placeholderLink > 0

    fun shouldHideHair() = equipment?.slot == 9 || appearanceOverride1 == 9 || appearanceOverride2 == 9

}

private fun Map<Int, Any>?.getString(key: Int): String {
    return when (val value = this?.get(key)) {
        is Int -> value.toString()
        is Number -> value.toString()
        is String -> value.toString() ?: ""
        else -> ""
    }
}

fun Map<Int, Any>?.getInt(key: Int): Int {
    return when (val value = this?.get(key)) {
        is Int -> value
        is Number -> value.toInt()
        is String -> value.toIntOrNull() ?: 0
        else -> 0
    }
}

