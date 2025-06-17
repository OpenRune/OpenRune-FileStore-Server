package dev.openrune.wiki.dumpers.impl

import dev.openrune.server.infobox.*
import dev.openrune.wiki.EncodingSettings
import dev.openrune.wiki.WikiDumper
import dev.openrune.wiki.dumpers.Dumper
import dev.openrune.wiki.dumpers.extractIds
import dev.openrune.wiki.dumpers.extractValueField
import io.github.oshai.kotlinlogging.KotlinLogging
import me.tongfei.progressbar.ProgressBarBuilder

class Npcs : Dumper {

    private val logger = KotlinLogging.logger {}
    private val npcMap = mutableMapOf<Int, InfoBoxNpc>()

    override fun name() = "npcs"

    override fun parseItem() {
        val parsedNpcs = mutableMapOf<Int, InfoBoxNpc>()

        val pages = WikiDumper.wiki.pages
            .asSequence()
            .filter { it.namespace.key == 0 }
            .filter {
                val text = it.revision.text.lowercase()
                text.contains("infobox monster") || text.contains("infobox npc")
            }

        val progressBar = ProgressBarBuilder()
            .setInitialMax(pages.count().toLong())
            .setTaskName("Dumping Npcs")
            .build()

        pages.forEach { page ->

            val templates = buildList {
                addAll(page.getTemplateMaps("infobox monster"))
                addAll(page.getTemplateMaps("infobox npc"))
            }

            if (templates.isEmpty()) return@forEach
            templates.forEach { template ->
                val ids = mutableListOf<Pair<Int, Int>>()
                val idKeys = template.keys.filter { it == "id" || it.matches(Regex("id\\d+")) }

                idKeys.indices.forEach { i ->
                    extractIds(template, i, ids)
                }

                ids.forEach { (id, keyIndex) ->

                    val examine = extractValueField("examine", template, keyIndex).orEmpty()

                    val aggressive = extractValueField("aggressive", template, keyIndex)
                        .orEmpty().contains("Yes", ignoreCase = true)

                    val poisonousText = extractValueField("poisonous", template, keyIndex).orEmpty()
                    val poisonousLevel = when {
                        "yes" !in poisonousText.lowercase() -> -1
                        Regex("""\((\d+)\)""").find(poisonousText) != null ->
                            Regex("""\((\d+)\)""").find(poisonousText)!!.groupValues[1].toInt()
                        else -> 1
                    }

                    val attackSpeed = extractValueField("attack speed", template, keyIndex).orEmpty().toIntOrNull() ?: 4
                    val respawn = extractValueField("respawn", template, keyIndex).orEmpty().toIntOrNull() ?: 35
                    val slayxp = extractValueField("slayxp", template, keyIndex).orEmpty().toIntOrNull() ?: -1

                    fun checkImmunity(field: String) =
                        extractValueField(field, template, keyIndex)
                            .orEmpty()
                            .contains(Regex("yes|immune", RegexOption.IGNORE_CASE))

                    val immunepoison = checkImmunity("immunepoison")
                    val immunevenom = checkImmunity("immunevenom")
                    val immunecannon = checkImmunity("immunecannon")
                    val immunethrall = checkImmunity("immunethrall")

                    val slayerMasters = extractValueField("assignedby", template, keyIndex)
                        ?.split(",")
                        ?.mapNotNull { SlayerMaster.entries.find { sm -> sm.name == it.trim().uppercase() } }
                        .orEmpty()

                    val maxHit = parseMaxHitField(extractValueField("max hit", template, keyIndex))

                    val elementalTypeIntMutableMap = mutableMapOf<ElementalType, Int>()

                    val elementalWeaknessTypeRaw = extractValueField("elementalweaknesstype", template, keyIndex)?.uppercase()
                    val elementalWeaknessPercentRaw = extractValueField("elementalweaknesspercent", template, keyIndex)

                    if (!elementalWeaknessTypeRaw.isNullOrBlank() && !elementalWeaknessPercentRaw.isNullOrBlank()) {
                        val elementalWeaknessType = runCatching { ElementalType.valueOf(elementalWeaknessTypeRaw) }.getOrNull()
                        val elementalWeaknessPercent = elementalWeaknessPercentRaw.toIntOrNull()

                        if (elementalWeaknessType != null && elementalWeaknessPercent != null) {
                            elementalTypeIntMutableMap[elementalWeaknessType] = elementalWeaknessPercent
                        }
                    }

                    val attributes = extractValueField("attributes", template, keyIndex)
                        ?.split(",")
                        ?.mapNotNull {
                            it.trim().takeIf { it.isNotEmpty() }
                                ?.uppercase()
                                ?.let { attr -> MonsterAttribute.entries.find { e -> e.name == attr } }
                        }.orEmpty()

                    val attack = extractValueField("attbns", template, keyIndex).orEmpty().toIntOrNull() ?: 0
                    val strength = extractValueField("strbns", template, keyIndex).orEmpty().toIntOrNull() ?: 0
                    val magic = extractValueField("amagic", template, keyIndex).orEmpty().toIntOrNull() ?: 0
                    val magicstrength = extractValueField("mbns", template, keyIndex).orEmpty().toIntOrNull() ?: 0
                    val rangeddefence = extractValueField("arange", template, keyIndex).orEmpty().toIntOrNull() ?: 0
                    val rangestrength = extractValueField("rngbns", template, keyIndex).orEmpty().toIntOrNull() ?: 0
                    val stabdefence = extractValueField("dstab", template, keyIndex).orEmpty().toIntOrNull() ?: 0
                    val slashdefence = extractValueField("dslash", template, keyIndex).orEmpty().toIntOrNull() ?: 0
                    val crushdefence = extractValueField("dcrush", template, keyIndex).orEmpty().toIntOrNull() ?: 0
                    val magicdefence = extractValueField("dmagic", template, keyIndex).orEmpty().toIntOrNull() ?: 0
                    val lightdefence = extractValueField("dlight", template, keyIndex).orEmpty().toIntOrNull() ?: 0
                    val standardefence = extractValueField("dstandard", template, keyIndex).orEmpty().toIntOrNull() ?: 0
                    val heavydefence = extractValueField("dheavy", template, keyIndex).orEmpty().toIntOrNull() ?: 0

                    val infoBoxNpc = InfoBoxNpc(
                        examine = examine,
                        maxHit = maxHit.first,
                        maxHitExtra = maxHit.second,
                        attributes = attributes,
                        immunepoison = immunepoison,
                        immunevenom = immunevenom,
                        immunecannon = immunecannon,
                        immunethrall = immunethrall,
                        aggressive = aggressive,
                        attackSpeed  = attackSpeed,
                        poisonousLevel = poisonousLevel,
                        slayerXp = slayxp,
                        respawn = respawn,
                        slayerMasters = slayerMasters,
                        elementalTypes = elementalTypeIntMutableMap,
                        attack = attack,
                        strength = strength,
                        magic = magic,
                        magicstrength = magicstrength,
                        rangeddefence = rangeddefence,
                        rangestrength = rangestrength,
                        stabdefence = stabdefence,
                        slashdefence = slashdefence,
                        crushdefence = crushdefence,
                        magicdefence = magicdefence,
                        lightdefence = lightdefence,
                        standardefence = standardefence,
                        heavydefence = heavydefence,
                    )

                    parsedNpcs[id] = infoBoxNpc
                }
            }

            progressBar.step()
        }

        progressBar.close()

        println(parsedNpcs.filterKeys { it == 5878 }.values.firstOrNull())
        this.npcMap.clear()
        this.npcMap.putAll(parsedNpcs)
    }

    private fun parseMaxHitField(input: String?): Pair<Map<MaxHitType, Int>, Map<String, Int>> {
        if (input == null || input.trim().isEmpty() ||
            input.contains("N/A", true) ||
            input.contains("Does not attack", true)
        ) return mapOf(MaxHitType.ALL to -1) to emptyMap()

        if (input.contains("Varies", true)) return mapOf(MaxHitType.ALL to 60) to emptyMap()

        val tempMap = mutableMapOf<String, Int>()
        val trimmed = input.trim()

        Regex("""([a-zA-Z/]+)\s*=\s*(\d+)""").findAll(trimmed).forEach { match ->
            val value = match.groupValues[2].toInt()
            match.groupValues[1].split("/").forEach { key ->
                tempMap[key.lowercase().trim()] = value
            }
        }

        Regex("""^\d+""").find(trimmed)?.value?.toIntOrNull()?.let {
            tempMap.putIfAbsent("all", it)
        }

        Regex("""(\d+)\s*\(\s*(?:\[\[\s*([^\]]+)\s*]]|([^)]+))\s*\)""")
            .findAll(trimmed)
            .forEach { match ->
                val value = match.groupValues[1].toInt()
                val rawKey = match.groupValues[2].ifEmpty { match.groupValues[3] }
                tempMap[rawKey.lowercase().trim()] = value
            }

        if (tempMap.isEmpty() && trimmed.all { it.isDigit() }) {
            return mapOf(MaxHitType.ALL to trimmed.toInt()) to emptyMap()
        }

        val enumMap = mutableMapOf<MaxHitType, Int>()
        val stringMap = mutableMapOf<String, Int>()

        tempMap.ifEmpty { mapOf("all" to -1) }.forEach { (key, value) ->
            MaxHitType.entries.find { it.name.equals(key, ignoreCase = true) }
                ?.let { enumMap[it] = value }
                ?: run { stringMap[key] = value }
        }

        return enumMap to stringMap
    }

    override fun toWrite(encodingSettings: EncodingSettings): Any {
        if (!encodingSettings.linkedIds) {
            val updates = mutableMapOf<Int, InfoBoxNpc>()

            npcMap.forEach { (id, item) ->
                val linkedIds = item.linkedIds.orEmpty()
                if (linkedIds.isNotEmpty()) {
                    val clearedItem = item.copy(linkedIds = emptyList())
                    linkedIds.forEach { updates[it] = clearedItem }
                }
            }

            updates.forEach { (id, copiedItem) ->
                npcMap[id] = copiedItem
            }
        }

        return npcMap.toSortedMap()
    }
}
