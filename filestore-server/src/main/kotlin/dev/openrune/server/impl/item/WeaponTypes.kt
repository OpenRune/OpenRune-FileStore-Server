package dev.openrune.server.impl.item

enum class CombatStyle {
    CHOP, SLASH,
    SMASH, BLOCK,
    HACK, LUNGE,
    SWIPE, POUND,
    PUMMEL, STAB,
    SPIKE, IMPALE,
    JAB, FEND,
    BASH, REAP,
    PUNCH, KICK,
    FLICK, LASH,
    DEFLECT, ACCURATE,
    RAPID,
    LONGRANGE,SHORT_FUSE,
    MEDIUM_FUSE,LONG_FUSE,
    AIM_AND_FIRE,SPELL,
    FOCUS,SCORCH,
    FLARE,BLAZE,
    MELEE,RANGED,
    MAGIC

}

enum class AttackType {
    CHOP, SLASH, CRUSH,STAB,NONE,STANDARD,HEAVY,LIGHT,MAGIC,RANGED
}

enum class FightStyle {
    ACCURATE,
    AGGRESSIVE,
    DEFENSIVE,
    CONTROLLED,
    RAPID,
    LONGRANGE,
    NONE,
    SHORT_FUSE,
    MEDIUM_FUSE,
    LONG_FUSE,DEFENSIVE_AUTOCAST,
    AUTOCAST
}

data class CombatStyleEntry(
    val combatStyle : CombatStyle,
    val attackType: AttackType,
    val fightStyle: FightStyle,
    val experience: List<Int> = emptyList(),
    val levelBoost: LevelBoost? = null
) {
    fun calcExp(hitAmount : Int) {

    }
}

data class LevelBoost(
    val amount: Int,
    val skills: List<Int>
)

enum class WeaponTypes(val varbitState : Int, val displayName : String,val fallbackRenderID : Int = 0,vararg combatStyleEntries : CombatStyleEntry) {
    `2H_SWORD`(varbitState = 10, displayName = "2h Sword", 57,
        CombatStyleEntry(combatStyle = CombatStyle.CHOP, attackType = AttackType.SLASH, fightStyle = FightStyle.ACCURATE, experience = listOf(0, 3), levelBoost = LevelBoost(3, listOf(0))),
        CombatStyleEntry(combatStyle = CombatStyle.SLASH, attackType = AttackType.SLASH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.SMASH, attackType = AttackType.CRUSH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.BLOCK, attackType = AttackType.SLASH, fightStyle = FightStyle.DEFENSIVE, experience = listOf(1, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    AXE(varbitState = 1, displayName = "Axe", 4,
        CombatStyleEntry(combatStyle = CombatStyle.CHOP, attackType = AttackType.SLASH, fightStyle = FightStyle.ACCURATE, experience = listOf(0, 3), levelBoost = LevelBoost(3, listOf(0))),
        CombatStyleEntry(combatStyle = CombatStyle.HACK, attackType = AttackType.SLASH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.SMASH, attackType = AttackType.CRUSH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.BLOCK, attackType = AttackType.SLASH, fightStyle = FightStyle.DEFENSIVE, experience = listOf(1, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    BANNER(varbitState = 23, displayName = "Banner", 19,
        CombatStyleEntry(combatStyle = CombatStyle.LUNGE, attackType = AttackType.STAB, fightStyle = FightStyle.ACCURATE, experience = listOf(0, 3), levelBoost = LevelBoost(3, listOf(0))),
        CombatStyleEntry(combatStyle = CombatStyle.SWIPE, attackType = AttackType.SLASH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.POUND, attackType = AttackType.CRUSH, fightStyle = FightStyle.CONTROLLED, experience = listOf(0, 2, 1, 3), levelBoost = LevelBoost(1, listOf(0, 2, 1))),
        CombatStyleEntry(combatStyle = CombatStyle.BLOCK, attackType = AttackType.STAB, fightStyle = FightStyle.DEFENSIVE, experience = listOf(1, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    BLUNT(varbitState = 2, displayName = "Blunt", 8,
        CombatStyleEntry(combatStyle = CombatStyle.POUND, attackType = AttackType.CRUSH, fightStyle = FightStyle.ACCURATE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.PUMMEL, attackType = AttackType.CRUSH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.BLOCK, attackType = AttackType.CRUSH, fightStyle = FightStyle.DEFENSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2)))
    ),

    BLUDGEON(varbitState = 25, displayName = "Bludgeon", 111,
        CombatStyleEntry(combatStyle = CombatStyle.POUND, attackType = AttackType.CRUSH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.PUMMEL, attackType = AttackType.CRUSH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.SMASH, attackType = AttackType.CRUSH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
    ),

    BULWARK(varbitState = 26, displayName = "Bulwark", 112,
        CombatStyleEntry(combatStyle = CombatStyle.PUMMEL, attackType = AttackType.CRUSH, fightStyle = FightStyle.ACCURATE, experience = listOf(0, 3), levelBoost = LevelBoost(3, listOf(0))),
        CombatStyleEntry(combatStyle = CombatStyle.BLOCK, attackType = AttackType.NONE, fightStyle = FightStyle.NONE)
    ),

    CLAW(varbitState = 4, displayName = "Claws", 42,
        CombatStyleEntry(combatStyle = CombatStyle.CHOP, attackType = AttackType.SLASH, fightStyle = FightStyle.ACCURATE, experience = listOf(0, 3), levelBoost = LevelBoost(3, listOf(0))),
        CombatStyleEntry(combatStyle = CombatStyle.SLASH, attackType = AttackType.SLASH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.LUNGE, attackType = AttackType.STAB, fightStyle = FightStyle.CONTROLLED, experience = listOf(0, 2, 1, 3), levelBoost = LevelBoost(1, listOf(0, 2, 1))),
        CombatStyleEntry(combatStyle = CombatStyle.BLOCK, attackType = AttackType.SLASH, fightStyle = FightStyle.DEFENSIVE, experience = listOf(1, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    PARTISAN(varbitState = 30, displayName = "Partisan", 0,
        CombatStyleEntry(combatStyle = CombatStyle.STAB, attackType = AttackType.STAB, fightStyle = FightStyle.ACCURATE, experience = listOf(0, 3), levelBoost = LevelBoost(3, listOf(0))),
        CombatStyleEntry(combatStyle = CombatStyle.LUNGE, attackType = AttackType.STAB, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.POUND, attackType = AttackType.CRUSH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.BLOCK, attackType = AttackType.STAB, fightStyle = FightStyle.DEFENSIVE, experience = listOf(1, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    PICKAXE(varbitState = 11, displayName = "Pickaxe", 60,
        CombatStyleEntry(combatStyle = CombatStyle.SPIKE, attackType = AttackType.STAB, fightStyle = FightStyle.ACCURATE, experience = listOf(0, 3), levelBoost = LevelBoost(3, listOf(0))),
        CombatStyleEntry(combatStyle = CombatStyle.IMPALE, attackType = AttackType.STAB, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.SMASH, attackType = AttackType.CRUSH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.BLOCK, attackType = AttackType.STAB, fightStyle = FightStyle.DEFENSIVE, experience = listOf(1, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    POLEARM(varbitState = 12, displayName = "Polearm", 61,
        CombatStyleEntry(combatStyle = CombatStyle.JAB, attackType = AttackType.STAB, fightStyle = FightStyle.CONTROLLED, experience = listOf(0, 2, 1, 3), levelBoost = LevelBoost(1, listOf(0, 2, 1))),
        CombatStyleEntry(combatStyle = CombatStyle.SWIPE, attackType = AttackType.SLASH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.FEND, attackType = AttackType.STAB, fightStyle = FightStyle.DEFENSIVE, experience = listOf(1, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    POLESTAFF(varbitState = 13, displayName = "Polestaff", 88,
        CombatStyleEntry(combatStyle = CombatStyle.BASH, attackType = AttackType.CRUSH, fightStyle = FightStyle.ACCURATE, experience = listOf(0, 3), levelBoost = LevelBoost(3, listOf(0))),
        CombatStyleEntry(combatStyle = CombatStyle.POUND, attackType = AttackType.CRUSH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.BLOCK, attackType = AttackType.CRUSH, fightStyle = FightStyle.DEFENSIVE, experience = listOf(1, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    SCYTHE(varbitState = 14, displayName = "Scythe", 64,
        CombatStyleEntry(combatStyle = CombatStyle.REAP, attackType = AttackType.SLASH, fightStyle = FightStyle.ACCURATE, experience = listOf(0, 3), levelBoost = LevelBoost(3, listOf(0))),
        CombatStyleEntry(combatStyle = CombatStyle.CHOP, attackType = AttackType.SLASH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.JAB, attackType = AttackType.CRUSH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.BLOCK, attackType = AttackType.SLASH, fightStyle = FightStyle.DEFENSIVE, experience = listOf(1, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    SLASH_SWORD(varbitState = 9, displayName = "Slash sword", 52,
        CombatStyleEntry(combatStyle = CombatStyle.CHOP, attackType = AttackType.SLASH, fightStyle = FightStyle.ACCURATE, experience = listOf(0, 3), levelBoost = LevelBoost(3, listOf(0))),
        CombatStyleEntry(combatStyle = CombatStyle.SLASH, attackType = AttackType.SLASH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.LUNGE, attackType = AttackType.STAB, fightStyle = FightStyle.CONTROLLED, experience = listOf(0, 2, 1, 3), levelBoost = LevelBoost(1, listOf(0, 2, 1))),
        CombatStyleEntry(combatStyle = CombatStyle.BLOCK, attackType = AttackType.SLASH, fightStyle = FightStyle.DEFENSIVE, experience = listOf(1, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    SPEAR(varbitState = 15, displayName = "Spear", 66,
        CombatStyleEntry(combatStyle = CombatStyle.LUNGE, attackType = AttackType.STAB, fightStyle = FightStyle.CONTROLLED, experience = listOf(0, 2, 1, 3), levelBoost = LevelBoost(1, listOf(0, 2, 1))),
        CombatStyleEntry(combatStyle = CombatStyle.SWIPE, attackType = AttackType.SLASH, fightStyle = FightStyle.CONTROLLED, experience = listOf(0, 2, 1, 3), levelBoost = LevelBoost(1, listOf(0, 2, 1))),
        CombatStyleEntry(combatStyle = CombatStyle.POUND, attackType = AttackType.CRUSH, fightStyle = FightStyle.CONTROLLED, experience = listOf(0, 2, 1, 3), levelBoost = LevelBoost(1, listOf(0, 2, 1))),
        CombatStyleEntry(combatStyle = CombatStyle.BLOCK, attackType = AttackType.STAB, fightStyle = FightStyle.DEFENSIVE, experience = listOf(1, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    SPIKED(varbitState = 16, displayName = "Spiked", 75,
        CombatStyleEntry(combatStyle = CombatStyle.POUND, attackType = AttackType.CRUSH, fightStyle = FightStyle.ACCURATE, experience = listOf(0, 3), levelBoost = LevelBoost(3, listOf(0))),
        CombatStyleEntry(combatStyle = CombatStyle.PUMMEL, attackType = AttackType.CRUSH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.SPIKE, attackType = AttackType.STAB, fightStyle = FightStyle.CONTROLLED, experience = listOf(0, 2, 1, 3), levelBoost = LevelBoost(1, listOf(0, 2, 1))),
        CombatStyleEntry(combatStyle = CombatStyle.BLOCK, attackType = AttackType.CRUSH, fightStyle = FightStyle.DEFENSIVE, experience = listOf(1, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    STAB_SWORD(varbitState = 17, displayName = "Stab sword", 80,
        CombatStyleEntry(combatStyle = CombatStyle.STAB, attackType = AttackType.STAB, fightStyle = FightStyle.ACCURATE, experience = listOf(0, 3), levelBoost = LevelBoost(3, listOf(0))),
        CombatStyleEntry(combatStyle = CombatStyle.LUNGE, attackType = AttackType.STAB, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.SLASH, attackType = AttackType.SLASH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.BLOCK, attackType = AttackType.STAB, fightStyle = FightStyle.DEFENSIVE, experience = listOf(1, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    UNARMED(varbitState = 0, displayName = "Unarmed", 0,
        CombatStyleEntry(combatStyle = CombatStyle.PUNCH, attackType = AttackType.CRUSH, fightStyle = FightStyle.ACCURATE, experience = listOf(0, 3), levelBoost = LevelBoost(3, listOf(0))),
        CombatStyleEntry(combatStyle = CombatStyle.KICK, attackType = AttackType.CRUSH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.BLOCK, attackType = AttackType.CRUSH, fightStyle = FightStyle.DEFENSIVE, experience = listOf(1, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    WHIP(varbitState = 20, displayName = "Whip", 105,
        CombatStyleEntry(combatStyle = CombatStyle.FLICK, attackType = AttackType.SLASH, fightStyle = FightStyle.ACCURATE, experience = listOf(0, 3), levelBoost = LevelBoost(3, listOf(0))),
        CombatStyleEntry(combatStyle = CombatStyle.LASH, attackType = AttackType.SLASH, fightStyle = FightStyle.CONTROLLED, experience = listOf(0, 2, 1, 3), levelBoost = LevelBoost(1, listOf(0, 2, 1))),
        CombatStyleEntry(combatStyle = CombatStyle.DEFLECT, attackType = AttackType.SLASH, fightStyle = FightStyle.DEFENSIVE, experience = listOf(1, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    BLASTER(varbitState = -1, displayName = "Blaster",
    ),

    BOW(varbitState = 3, displayName = "Bow", 36,
        CombatStyleEntry(combatStyle = CombatStyle.ACCURATE, attackType = AttackType.STANDARD, fightStyle = FightStyle.ACCURATE, experience = listOf(4, 3), levelBoost = LevelBoost(3, listOf(4))),
        CombatStyleEntry(combatStyle = CombatStyle.RAPID, attackType = AttackType.STANDARD, fightStyle = FightStyle.RAPID, experience = listOf(4, 3)),
        CombatStyleEntry(combatStyle = CombatStyle.LONGRANGE, attackType = AttackType.STANDARD, fightStyle = FightStyle.LONGRANGE, experience = listOf(4, 1, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    CHINCHOMPAS(varbitState = 7, displayName = "Chinchompas", 50,
        CombatStyleEntry(combatStyle = CombatStyle.SHORT_FUSE, attackType = AttackType.HEAVY, fightStyle = FightStyle.SHORT_FUSE, experience = listOf(4, 3), levelBoost = LevelBoost(3, listOf(4))),
        CombatStyleEntry(combatStyle = CombatStyle.MEDIUM_FUSE, attackType = AttackType.HEAVY, fightStyle = FightStyle.MEDIUM_FUSE, experience = listOf(4, 3)),
        CombatStyleEntry(combatStyle = CombatStyle.LONG_FUSE, attackType = AttackType.HEAVY, fightStyle = FightStyle.LONG_FUSE, experience = listOf(4, 1, 3))
    ),

    CROSSBOW(varbitState = 5, displayName = "Crossbow", 45,
        CombatStyleEntry(combatStyle = CombatStyle.ACCURATE, attackType = AttackType.HEAVY, fightStyle = FightStyle.ACCURATE, experience = listOf(4, 3), levelBoost = LevelBoost(3, listOf(4))),
        CombatStyleEntry(combatStyle = CombatStyle.RAPID, attackType = AttackType.HEAVY, fightStyle = FightStyle.RAPID, experience = listOf(4, 3)),
        CombatStyleEntry(combatStyle = CombatStyle.LONGRANGE, attackType = AttackType.HEAVY, fightStyle = FightStyle.LONGRANGE, experience = listOf(4, 1, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    GUN(varbitState = 8, displayName = "Gun", 51,
        CombatStyleEntry(combatStyle = CombatStyle.AIM_AND_FIRE, attackType = AttackType.NONE, fightStyle = FightStyle.NONE),
        CombatStyleEntry(combatStyle = CombatStyle.KICK, attackType = AttackType.CRUSH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2)))
    ),

    THROWN(varbitState = 19, displayName = "Thrown", 98,
        CombatStyleEntry(combatStyle = CombatStyle.ACCURATE, attackType = AttackType.LIGHT, fightStyle = FightStyle.ACCURATE, experience = listOf(4, 3), levelBoost = LevelBoost(3, listOf(4))),
        CombatStyleEntry(combatStyle = CombatStyle.RAPID, attackType = AttackType.LIGHT, fightStyle = FightStyle.RAPID, experience = listOf(4, 3)),
        CombatStyleEntry(combatStyle = CombatStyle.LONGRANGE, attackType = AttackType.LIGHT, fightStyle = FightStyle.LONGRANGE, experience = listOf(4, 1, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    BLADED_STAFF(varbitState = 21, displayName = "Bladed Staff", 6,
        CombatStyleEntry(combatStyle = CombatStyle.JAB, attackType = AttackType.STAB, fightStyle = FightStyle.ACCURATE, experience = listOf(0, 3), levelBoost = LevelBoost(3, listOf(0))),
        CombatStyleEntry(combatStyle = CombatStyle.SWIPE, attackType = AttackType.SLASH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.FEND, attackType = AttackType.CRUSH, fightStyle = FightStyle.DEFENSIVE, experience = listOf(1, 3), levelBoost = LevelBoost(3, listOf(1))),
        CombatStyleEntry(combatStyle = CombatStyle.SPELL, attackType = AttackType.MAGIC, fightStyle = FightStyle.DEFENSIVE_AUTOCAST, experience = listOf(6, 3, 1)),
        CombatStyleEntry(combatStyle = CombatStyle.SPELL, attackType = AttackType.MAGIC, fightStyle = FightStyle.AUTOCAST, experience = listOf(6, 3))
    ),

    POWERED_STAFF(varbitState = 22, displayName = "Powered Staff", 107,
        CombatStyleEntry(combatStyle = CombatStyle.ACCURATE, attackType = AttackType.MAGIC, fightStyle = FightStyle.ACCURATE, experience = listOf(6, 3), levelBoost = LevelBoost(3, listOf(6))),
        CombatStyleEntry(combatStyle = CombatStyle.ACCURATE, attackType = AttackType.MAGIC, fightStyle = FightStyle.ACCURATE, experience = listOf(6, 3), levelBoost = LevelBoost(3, listOf(6))),
        CombatStyleEntry(combatStyle = CombatStyle.LONGRANGE, attackType = AttackType.MAGIC, fightStyle = FightStyle.LONGRANGE, experience = listOf(6, 1, 3), levelBoost = LevelBoost(1, listOf(6, 1)))
    ),

    POWERED_WAND(varbitState = 29, displayName = "Powered Wand", 93,
        CombatStyleEntry(combatStyle = CombatStyle.ACCURATE, attackType = AttackType.MAGIC, fightStyle = FightStyle.ACCURATE, experience = listOf(6, 3), levelBoost = LevelBoost(3, listOf(6))),
        CombatStyleEntry(combatStyle = CombatStyle.ACCURATE, attackType = AttackType.MAGIC, fightStyle = FightStyle.ACCURATE, experience = listOf(6, 3), levelBoost = LevelBoost(3, listOf(6))),
        CombatStyleEntry(combatStyle = CombatStyle.LONGRANGE, attackType = AttackType.MAGIC, fightStyle = FightStyle.LONGRANGE, experience = listOf(6, 1, 3), levelBoost = LevelBoost(1, listOf(6, 1)))
    ),

    STAFF(varbitState = 18, displayName = "Staff", 143,
        CombatStyleEntry(combatStyle = CombatStyle.BASH, attackType = AttackType.CRUSH, fightStyle = FightStyle.ACCURATE, experience = listOf(0, 3), levelBoost = LevelBoost(3, listOf(0))),
        CombatStyleEntry(combatStyle = CombatStyle.POUND, attackType = AttackType.CRUSH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.FOCUS, attackType = AttackType.CRUSH, fightStyle = FightStyle.DEFENSIVE, experience = listOf(1, 3), levelBoost = LevelBoost(3, listOf(1))),
        CombatStyleEntry(combatStyle = CombatStyle.SPELL, attackType = AttackType.MAGIC, fightStyle = FightStyle.DEFENSIVE_AUTOCAST, experience = listOf(6, 3, 1)),
        CombatStyleEntry(combatStyle = CombatStyle.SPELL, attackType = AttackType.MAGIC, fightStyle = FightStyle.AUTOCAST, experience = listOf(6, 3))
    ),

    SALAMANDER(varbitState = 6, displayName = "Salamander", 49,
        CombatStyleEntry(combatStyle = CombatStyle.SCORCH, attackType = AttackType.SLASH, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.FLARE, attackType = AttackType.STANDARD, fightStyle = FightStyle.ACCURATE, experience = listOf(4, 3)),
        CombatStyleEntry(combatStyle = CombatStyle.BLAZE, attackType = AttackType.MAGIC, fightStyle = FightStyle.DEFENSIVE, experience = listOf(6, 3), levelBoost = LevelBoost(3, listOf(1)))
    ),

    `MULTI-STYLE`(varbitState = 31, displayName = "Multi Style", 49,
        CombatStyleEntry(combatStyle = CombatStyle.MELEE, attackType = AttackType.STAB, fightStyle = FightStyle.AGGRESSIVE, experience = listOf(2, 3), levelBoost = LevelBoost(3, listOf(2))),
        CombatStyleEntry(combatStyle = CombatStyle.RANGED, attackType = AttackType.RANGED, fightStyle = FightStyle.RAPID, experience = listOf(4, 3)),
        CombatStyleEntry(combatStyle = CombatStyle.MAGIC, attackType = AttackType.MAGIC, fightStyle = FightStyle.DEFENSIVE, experience = listOf(6, 3), levelBoost = LevelBoost(3, listOf(1)))
    );

    val combatStyleList: Array<out CombatStyleEntry> = combatStyleEntries

}

