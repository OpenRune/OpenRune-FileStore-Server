package dev.openrune.definition

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
    override var appearanceOverride1: Int = -1,
    override var appearanceOverride2: Int = -1,
    var examine : String = "",
    var destroy: String = "",
    var alchable: Boolean = true,
    var exchangeCost: Int = -1,

    var equipment: Equipment? = null,
    var weapon: Weapon? = null,
    override var params: MutableMap<String, Any>? = null
) : ItemServerBase()

data class EquipmentStats(
    var attackStab: Int = 0,
    var attackSlash: Int = 0,
    var attackCrush: Int = 0,
    var attackMagic: Int = 0,
    var attackRanged: Int = 0,
    var defenceStab: Int = 0,
    var defenceSlash: Int = 0,
    var defenceCrush: Int = 0,
    var defenceMagic: Int = 0,
    var defenceRanged: Int = 0,
    var meleeStrength: Int = 0,
    var prayer: Int = 0,
    var rangedStrength: Int = 0,
    var magicStrength: Int = 0,
    var rangedDamage: Int = 0,
    var magicDamage: Int = 0,
    var demonDamage: Int = 0,
    var degradeable: Int = 0,
    var silverStrength: Int = 0,
    var corpBoost: Int = 0,
    var golemDamage: Int = 0,
    var kalphiteDamage: Int = 0
)


data class Equipment(
    var slot: Int = -1,
    var requirements: Map<String, Int> = emptyMap(),
    var stats: EquipmentStats? = null
) {

    //val equipmentOptions: List<String?> by lazy {
      //  (0 until 7).map { cachedParams.getString(451 + it).takeIf { it.isNotEmpty() } }
    //}
}


data class Weapon(
    var weaponTypeRenderData: WeaponTypeRenderData? = null,
    var weaponType: WeaponTypes = WeaponTypes.UNARMED,
    var attackSpeed: Int = 0,
    var attackRange: Int = 0,
    var specAmount: Int = -1
) {
    fun hasSpec() = specAmount != -1
}


private fun Map<String, Any?>.getString(key: Int): String =
    this[key.toString()]?.toString() ?: ""

fun Map<String, Any?>.getInt(key: Int): Int = when (val v = this[key.toString()]) {
    is Int -> v
    is Number -> v.toInt()
    is String -> v.toIntOrNull() ?: 0
    else -> 0
}

