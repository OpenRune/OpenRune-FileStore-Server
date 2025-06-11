package dev.openrune.wiki.dumpers.impl

import com.google.gson.*
import dev.openrune.cache.CacheManager
import dev.openrune.server.infobox.InfoBoxItem
import dev.openrune.wiki.EncodingSettings
import dev.openrune.wiki.Wiki
import dev.openrune.wiki.WikiDumper
import dev.openrune.wiki.dumpers.Dumper
import dev.openrune.wiki.dumpers.extractIds
import dev.openrune.wiki.dumpers.extractValueField
import dev.openrune.wiki.dumpers.GrandExchangePrices
import dev.openrune.wiki.dumpers.ItemRequirementsManager
import io.github.oshai.kotlinlogging.KotlinLogging
import me.tongfei.progressbar.ProgressBarBuilder
import java.lang.reflect.Type

class InfoBoxItemSerializer : JsonSerializer<InfoBoxItem> {
    override fun serialize(src: InfoBoxItem, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()

        if (!src.linkedIds.isNullOrEmpty()) {
            jsonObject.add("linkedIds", context.serialize(src.linkedIds))
        }

        if (src.examine != "null") {
            jsonObject.add("examine", context.serialize(src.examine))
        }

        if (src.cost != -1) {
            jsonObject.add("cost", context.serialize(src.cost))
        }

        if (src.exchangeCost != -1) {
            jsonObject.add("exchangeCost", context.serialize(src.exchangeCost))
        }

        if (src.attackRange != 0) {
            jsonObject.add("attackRange", context.serialize(src.attackRange))
        }

        if (src.combatStyle != "null") {
            jsonObject.add("combatStyle", context.serialize(src.combatStyle))
        }

        if (src.destroy != "null") {
            jsonObject.add("destroy", context.serialize(src.destroy))
        }

        if (!src.alchable) {
            jsonObject.add("alchable", context.serialize(src.alchable))
        }

        if (src.itemReq.isNotEmpty()) {
            jsonObject.add("itemReq", context.serialize(src.itemReq))
        }

        return jsonObject
    }
}


class Items : Dumper {

    private val logger = KotlinLogging.logger {}

    override fun name() = "items"
    var items = mutableMapOf<Int, InfoBoxItem>()

    override fun parseItem() {
        val parsedItems = mutableMapOf<Int, InfoBoxItem>()

        val pages = WikiDumper.wiki.pages
            .asSequence()
            .filter { it.namespace.key == 0 }
            .filter {
                it.revision.text.contains("infobox item", ignoreCase = true) ||
                        it.revision.text.contains("infobox bonuses", ignoreCase = true) ||
                        it.revision.text.contains("CombatStyles", ignoreCase = true)
            }

        val pb = ProgressBarBuilder().setInitialMax((pages.count() + 3).toLong()).setTaskName("Dumping Items").build()

        pb.extraMessage = "Fetching Prices"
        val prices = GrandExchangePrices()
        prices.fetchLatestPrices()
        pb.step()
        pb.extraMessage = "Fetching Item Req"
        val skillReq = ItemRequirementsManager()
        skillReq.load()
        pb.step()
        pb.extraMessage = "Processing Item Pages"
        pages.forEach { page ->
            val templatesInfoBox = page.getTemplateMaps("infobox item")
            if (templatesInfoBox.isEmpty()) return@forEach
            val templateCombatBonuses = page.getTemplateMaps("infobox bonuses")
            val combatStylesTemplates = page.getTemplateMaps("CombatStyles")
            val template = templatesInfoBox.first()
            val templateCombatBonues = templateCombatBonuses.firstOrNull() ?: emptyMap()
            val combatStylesTemplate = combatStylesTemplates.firstOrNull() ?: emptyMap()

            val idKeys = template.keys.filter { it == "id" || it.matches(Regex("id\\d+")) }
            val idCount = idKeys.size

            val ids = mutableListOf<Pair<Int, Int>>()
            for (keyIndex in 1..idCount) {
                extractIds(template, keyIndex, ids)
            }

            var attackRange = 0
            var combatStyle = "null"
            if (templateCombatBonues.isNotEmpty()) {
                attackRange = when (val value = extractValueField("attackrange", templateCombatBonues, 0)) {
                    "staff" -> 10
                    else -> value?.toIntOrNull() ?: 0
                }
                combatStyle = extractValueField("combatstyle", templateCombatBonues, 0).orEmpty()
                if (combatStyle == "null") { // Fallback as a few items dont have it in the bonues for some very odd reason
                    combatStyle = combatStylesTemplate[""].toString()
                    if (attackRange == -1) {
                        attackRange = when (val value = extractValueField("attackrange", templateCombatBonues, 0)) {
                            "staff" -> 10
                            else -> value?.toIntOrNull() ?: 0
                        }
                    }
                }
            }

            ids.forEach { (id, keyIndex) ->
                val examine = extractValueField("examine", template, keyIndex).orEmpty()
                val cost = extractValueField("value", template, keyIndex)?.toIntOrNull() ?: -1
                val destroy = extractValueField("destroy", template, keyIndex).orEmpty()

                val attackRangeTemp =
                    CacheManager.getItem(id)?.params?.takeIf { it.containsKey(13.toString()) }?.let { 0 } ?: attackRange

                var alchable = true
                if ("alchable" in template) {
                    alchable = extractValueField("alchable", template, keyIndex) == "Yes"
                }
                if (cost == 0) alchable = false

                val skillReqMap = skillReq.getRequirementsForItem(id)

                val newItem = InfoBoxItem(
                    emptyList(), examine, cost, prices.getPriceData(id), destroy, alchable, attackRangeTemp, combatStyle,
                    skillReqMap ?: emptyMap()
                )
                val existing = parsedItems.entries.find { it.value.hashCode() == newItem.hashCode() }

                if (existing != null) {
                    val existingItem = existing.value
                    if (existing.key != id && id !in (existingItem.linkedIds ?: emptyList())) {
                        parsedItems[existing.key] = existingItem.copy(
                            linkedIds = (existingItem.linkedIds ?: emptyList()) + id
                        )
                    }
                } else {
                    parsedItems[id] = newItem
                }
            }
            pb.step()
        }
        pb.close()
        this.items = parsedItems
    }

    override fun toWrite(encodingSettings: EncodingSettings): Any {
        if (!encodingSettings.linkedIds) {
            val updates = mutableMapOf<Int, InfoBoxItem>()

            items.forEach { (id, item) ->
                val linkedIds = item.linkedIds.orEmpty()
                if (linkedIds.isNotEmpty()) {
                    val clearedItem = item.copy(linkedIds = emptyList())
                    linkedIds.forEach { linkedId -> updates[linkedId] = clearedItem }
                }
            }

            updates.forEach { (id, copiedItem) ->
                items[id] = copiedItem
            }
        }

        return items.toSortedMap()
    }

}