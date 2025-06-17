package dev.openrune.server.impl

import dev.openrune.definition.Definition
import dev.openrune.definition.type.NpcType
import dev.openrune.server.infobox.*

data class NpcServerType(
    override var id: Int = -1,
    var name : String = "",
    var examine: String = "",
    var size: Int = 1,
    var category: Int = -1,
    var standAnim: Int = -1,
    var rotateLeftAnim: Int = -1,
    var rotateRightAnim: Int = -1,
    var walkAnim: Int = -1,
    var rotateBackAnim: Int = -1,
    var walkLeftAnim: Int = -1,
    var walkRightAnim: Int = -1,
    var actions: MutableList<String?> = mutableListOf(null, null, null, null, null),
    var varbit: Int = -1,
    var varp: Int = -1,
    var transforms: MutableList<Int>? = null,
    var combatLevel: Int = -1,
    var hasRenderPriority: Boolean = false,
    var lowPriorityFollowerOps: Boolean = false,
    var isFollower: Boolean = false,
    var runSequence: Int = -1,
    var isInteractable : Boolean = true,
    var runBackSequence: Int = -1,
    var runRightSequence: Int = -1,
    var runLeftSequence: Int = -1,
    var crawlSequence: Int = -1,
    var crawlBackSequence: Int = -1,
    var crawlRightSequence: Int = -1,
    var crawlLeftSequence: Int = -1,
    var params: MutableMap<String, Any>? = null,

    var combat: CombatInfo? = null
) : Definition {

    data class CombatInfo(
        val bonuses: Bonuses = Bonuses(),
        val stats : Stats = Stats(),
        val immunities : Immunities = Immunities(),
        val slayer : Slayer? = null,

        val maxHit: Map<MaxHitType,Int>,
        val maxHitExtra: Map<String,Int>,
        val attributes: List<MonsterAttribute>,


        val aggressive : Boolean = false,
        val attackSpeed : Int = 4,
        val poisonousLevel  : Int = -1,
        val respawn : Int = -1,
        val elementalTypes : Map<ElementalType,Int> = emptyMap(),
    ) {

        data class Bonuses(
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
        )

        data class Stats(
            val attack : Int = 0,
            val strength: Int = 0,
            val defence : Int = 0,
            val magic : Int = 0,
            val ranged : Int = 0,
            val hitpoints : Int = 0,
        )

        data class Slayer(
            val slayerXp : Int = 1,
            val slayerMasters : List<SlayerMaster> = emptyList()
        )

        data class Immunities(
            val immunepoison : Boolean = false,
            val immunevenom : Boolean = false,
            val immunecannon : Boolean = false,
            val immunethrall : Boolean = false,
        )

    }

    companion object {
        fun load(id: Int, infoboxNpc: InfoBoxNpc?, cache : NpcType): NpcServerType {

            return NpcServerType().apply {
                this.id = id
                name = cache.name
                examine = infoboxNpc?.examine?: ""
                size = cache.size
                isInteractable = cache.isInteractable
                category = cache.category
                standAnim = cache.standAnim
                rotateLeftAnim = cache.rotateLeftAnim
                rotateRightAnim = cache.rotateRightAnim
                walkAnim = cache.walkAnim
                rotateBackAnim = cache.rotateBackAnim
                walkLeftAnim = cache.walkLeftAnim
                walkRightAnim = cache.walkRightAnim
                actions = cache.actions
                varbit = cache.varbit
                varp = cache.varp
                transforms = cache.transforms
                combatLevel = cache.combatLevel
                lowPriorityFollowerOps = cache.lowPriorityFollowerOps
                isFollower = cache.isFollower
                runSequence = cache.runSequence
                runBackSequence = cache.runBackSequence
                runRightSequence = cache.runRightSequence
                runLeftSequence = cache.runLeftSequence
                crawlSequence = cache.crawlSequence
                crawlBackSequence = cache.crawlBackSequence
                crawlRightSequence = cache.crawlRightSequence
                crawlLeftSequence = cache.crawlLeftSequence
                params = cache.params

                val isMonster = listOf(
                    cache.attack,
                    cache.defence,
                    cache.strength,
                    cache.hitpoints,
                    cache.ranged,
                    cache.magic,
                ).any { it != 1 }

                if (isMonster && infoboxNpc != null) {

                    val slayer : CombatInfo.Slayer? = if (infoboxNpc.slayerMasters.isNotEmpty()) {
                        CombatInfo.Slayer(infoboxNpc.slayerXp, infoboxNpc.slayerMasters)
                    } else null

                    combat = CombatInfo(
                        stats = CombatInfo.Stats(
                            attack = cache.attack,
                            defence = cache.defence,
                            strength = cache.strength,
                            hitpoints = cache.hitpoints,
                            ranged = cache.ranged,
                            magic = cache.magic,
                        ),
                        bonuses = CombatInfo.Bonuses(
                            infoboxNpc.attack,
                            infoboxNpc.strength,
                            infoboxNpc.magic,
                            infoboxNpc.magicstrength,
                            infoboxNpc.rangeddefence,
                            infoboxNpc.rangestrength,
                            infoboxNpc.stabdefence,
                            infoboxNpc.slashdefence,
                            infoboxNpc.crushdefence,
                            infoboxNpc.magicdefence,
                            infoboxNpc.lightdefence,
                            infoboxNpc.standardefence,
                            infoboxNpc.heavydefence,
                        ),
                        maxHit = infoboxNpc.maxHit,
                        maxHitExtra = infoboxNpc.maxHitExtra,
                        attributes = infoboxNpc.attributes,
                        aggressive = infoboxNpc.aggressive,
                        attackSpeed = infoboxNpc.attackSpeed,
                        poisonousLevel = infoboxNpc.poisonousLevel,
                        respawn = infoboxNpc.respawn,

                        immunities = CombatInfo.Immunities(
                            immunepoison = infoboxNpc.immunepoison,
                            immunevenom = infoboxNpc.immunevenom,
                            immunecannon = infoboxNpc.immunecannon,
                            immunethrall = infoboxNpc.immunethrall,
                        ),
                        slayer = slayer
                    )
                }

            }
        }
    }

}

