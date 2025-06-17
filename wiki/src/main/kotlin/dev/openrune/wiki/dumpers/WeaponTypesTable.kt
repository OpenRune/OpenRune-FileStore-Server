package dev.openrune.wiki.dumpers

import dev.openrune.server.impl.item.AttackType
import dev.openrune.server.impl.item.CombatStyle
import dev.openrune.server.impl.item.CombatStyleEntry
import dev.openrune.server.impl.item.FightStyle
import dev.openrune.server.impl.item.LevelBoost
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File

object WeaponTypesTable  {

    @JvmStatic
    fun main(args : Array<String>) {
        val url = "https://oldschool.runescape.wiki/w/Weapons/Types"
        val doc: Document = Jsoup.connect(url).get()

        val result = mutableMapOf<String, List<CombatStyleEntry>>()

        var currentHeader: String? = null

        for (element in doc.body().select("*")) {
            if (element.tagName() == "h3") {
                val span = element.selectFirst("span.mw-headline")
                if (span != null) {
                    currentHeader = span.text()
                }
            }

            if (element.tagName() == "table" && element.hasClass("combat-styles")) {
                val styles = parseCombatStylesTable(element.outerHtml())
                if (currentHeader != null) {
                    result[currentHeader] = styles["combatStyles"] ?: emptyList()
                }
            }
        }

        formatAsEnum(result, "./dev.openrune.wiki.dumpers.WeaponTypesTable.txt")
    }

    fun parseCombatStylesTable(html: String): Map<String, List<CombatStyleEntry>> {
        val doc: Document = Jsoup.parse(html)
        val table = doc.selectFirst("table.combat-styles") ?: return emptyMap()
        val rows = table.select("tr").drop(1)

        val styles = rows.mapNotNull { row ->
            val cells = row.select("td")
            if (cells.size < 6) return@mapNotNull null

            val experienceTypeMap = mapOf(
                "attack" to 0,
                "strength" to 2,
                "ranged" to 4,
                "magic" to 6,
                "defence" to 1,
                "hitpoints" to 3
            )

            val styleName = cells[1].text()
            val attackType = cells[2].text()
            val weaponStyle = cells[3].text()
            val experience = cells[4].select("a").mapNotNull {
                val title = it.attr("title")
                experienceTypeMap[title.lowercase()]
            }
            val levelBoostText = cells[5].text().lowercase()

            val levelBoostAmount = Regex("""\+(\d+)""")
                .find(levelBoostText)
                ?.groupValues?.get(1)?.toIntOrNull() ?: 0

            val boostedSkills = experienceTypeMap.keys.filter { levelBoostText.contains(it) }
                .mapNotNull { experienceTypeMap[it] }

            CombatStyleEntry(
                combatStyle = CombatStyle.valueOf(styleName.uppercase().replace(" ", "_")),
                attackType = AttackType.valueOf(attackType.uppercase().replace(" ", "_")),
                fightStyle = FightStyle.valueOf(weaponStyle.uppercase().replace(" ", "_")),
                experience = experience,
                levelBoost = LevelBoost(
                    amount = levelBoostAmount,
                    skills = boostedSkills
                )
            )
        }

        return mapOf("combatStyles" to styles)
    }

    val attackStyleMeta = mapOf(
        "UNARMED" to Pair(0, "Unarmed"),
        "AXE" to Pair(1, "Axe"),
        "BLUNT" to Pair(2, "Blunt"),
        "BOW" to Pair(3, "Bow"),
        "CLAW" to Pair(4, "Claws"),
        "CROSSBOW" to Pair(5, "Crossbow"),
        "SALAMANDER" to Pair(6, "Salamander"),
        "CHINCHOMPA" to Pair(7, "Chinchompas"),
        "GUN" to Pair(8, "Gun"),
        "SLASH_SWORD" to Pair(9, "Slash sword"),
        "TWO_HANDED_SWORD" to Pair(10, "Two-handed sword"),
        "PICKAXE" to Pair(11, "Pickaxe"),
        "POLEARM" to Pair(12, "Polearm"),
        "POLESTAFF" to Pair(13, "Polestaff"),
        "SCYTHE" to Pair(14, "Scythe"),
        "SPEAR" to Pair(15, "Spear"),
        "SPIKED" to Pair(16, "Spiked"),
        "STAB_SWORD" to Pair(17, "Stab sword"),
        "STAFF" to Pair(18, "Staff"),
        "THROWN" to Pair(19, "Thrown"),
        "WHIP" to Pair(20, "Whip"),
        "BLADED_STAFF" to Pair(21, "Bladed Staff"),
        "POWERED_STAFF" to Pair(22, "Powered Staff"),
        "BANNER" to Pair(23, "Banner"),
        "Bludgeon" to Pair(25, "Bludgeon"),
        "BULWARK" to Pair(26, "Bulwark"),
        "PARTISAN" to Pair(30, "Partisan"),

        //Not Used
        "BLASTER" to Pair(-1, "Blaster"),
        "POWERED_WAND" to Pair(29, "Powered Wand"),
        "MULTI_STYLE" to Pair(31, "Multi Style"),
    )

    fun getAttackStyleMeta(name: String): Pair<Int, String> {
        return attackStyleMeta[name] ?: error("Invalid attack style: $name")
    }

    fun formatAsEnum(result: Map<String, List<CombatStyleEntry>>, outputFile: String) {
        val stringBuilder = StringBuilder()
        stringBuilder.appendLine("enum class WeaponTypes(varbitState : Int, displayName : String,vararg combatEntry : CombatEntry) {")
        result.forEach { (header, styles) ->
            // Formatting the name for enum (removing spaces, making it uppercase)
            val typeName = header.uppercase()
                .replace(Regex("[^A-Z0-9]"), "_")
                .replace(Regex("_+"), "_")
                .trim('_')


            val metaData = getAttackStyleMeta(typeName)
            stringBuilder.appendLine("${typeName}(varbitState = ${metaData.first}, displayName = \"${metaData.second}\",")

            // Iterate over styles and add each style in the required format
            styles.forEachIndexed { index, style ->
                val exp = style.experience?.takeIf { it.isNotEmpty() }?.joinToString(", ")?.let { "experience = listOf($it)" }
                val levelBoost = style.levelBoost?.takeIf {
                    it.amount > 0 && it.skills.isNotEmpty()
                }?.let {
                    "levelBoost = LevelBoost(${it.amount}, listOf(${it.skills.joinToString(", ")}))"
                }

                // Append the style to the StringBuilder, checking for non-empty values
                stringBuilder.append("    CombatStyleEntry(combatStyle = CombatStyle.${style.combatStyle}, attackType = AttackType.${style.attackType}, fightStyle = FightStyle.${style.fightStyle}")
                if (!exp.isNullOrEmpty()) stringBuilder.append(", $exp")
                if (!levelBoost.isNullOrEmpty()) stringBuilder.append(", $levelBoost")
                stringBuilder.appendLine(")${if (index < styles.size - 1) "," else ""}")
            }

            // Close the enum declaration
            stringBuilder.appendLine("),\n")
        }
        stringBuilder.appendLine("}\n")
        // Write the formatted string to a file
        File(outputFile).writeText(stringBuilder.toString())
    }

}
