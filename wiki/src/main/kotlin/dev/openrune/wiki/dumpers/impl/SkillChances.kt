package dev.openrune.wiki.dumpers.impl

import dev.openrune.wiki.EncodingSettings
import dev.openrune.wiki.WikiDumper
import dev.openrune.wiki.dumpers.Dumper
import dev.openrune.wiki.dumpers.extractIds
import kotlin.sequences.forEach

class SkillChances: Dumper {
    data class SkillChance(val title: String, val names: List<String> = mutableListOf(), val entries: List<Map<String, Any>>)

    private val chances = mutableListOf<SkillChance>()

    override fun name(): String = "skillchances"

    override fun parseItem() {
        val pages = WikiDumper.wiki.pages
            .asSequence()
            .filter { it.namespace.key == 0 }
            .filter {
                val text = it.revision.text.lowercase()
                text.contains("skilling success chart")
            }

        pages.forEach { page ->
            val template = page.getTemplateMaps("skilling success chart")
            val templatesInfoBox = page.getTemplateMaps("infobox item")
            //items, npcs, objects
            val names = mutableListOf<String>()

            if(!templatesInfoBox.isEmpty()) {
                val templateItem = templatesInfoBox.first()
                val idKeys = templateItem.keys.filter { it == "id" || it.matches(Regex("id\\d+")) }
                val ids = mutableListOf<Pair<Int, Int>>()
                idKeys.indices.forEach { i ->
                    extractIds(templateItem, i, ids)
                }
                ids.forEach { (id, keyIndex) ->
                    names.add("items."+WikiDumper.itemGameVals!![id].name)
                }
            }
            val templatesNpc = buildList {
                addAll(page.getTemplateMaps("infobox monster"))
                addAll(page.getTemplateMaps("infobox npc"))
            }
            if(!templatesNpc.isEmpty()) {
                templatesNpc.forEach { templateNpc ->
                    val ids = mutableListOf<Pair<Int, Int>>()
                    val idKeys = templateNpc.keys.filter { it == "id" || it.matches(Regex("id\\d+")) }

                    idKeys.indices.forEach { i ->
                        extractIds(templateNpc, i, ids)
                    }
                    ids.forEach { (id, keyIndex) ->
                        names.add("npcs."+WikiDumper.npcGameVals!![id].name)
                    }
                }
            }
            val templatesObjects = buildList {
                addAll(page.getTemplateMaps("infobox scenery"))
            }
            if(!templatesObjects.isEmpty()) {
                templatesObjects.forEach { templateObject ->
                    val ids = mutableListOf<Pair<Int, Int>>()
                    val idKeys = templateObject.keys.filter { it == "id" || it.matches(Regex("id\\d+")) }

                    idKeys.indices.forEach { i ->
                        extractIds(templateObject, i, ids)
                    }
                    ids.forEach { (id, keyIndex) ->
                        names.add("objects."+ WikiDumper.objectGameVals!![id].name)
                    }
                }
            }
            if(template.size > 0) {
                chances.add(SkillChance(
                    title = page.title,
                    names = names,
                    entries = template
                ))

            }
        }
    }

    override fun toWrite(encodingSettings: EncodingSettings): Any {
        return chances
    }
}