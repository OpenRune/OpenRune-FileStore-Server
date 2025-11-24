package dev.openrune.wiki.skillchances

import com.google.gson.GsonBuilder
import dev.openrune.cache.gameval.GameValHandler
import dev.openrune.definition.GameValGroupTypes.*
import dev.openrune.filesystem.Cache
import dev.openrune.wiki.Wiki
import dev.openrune.wiki.dumpers.extractIds
import java.io.File

object SkillChanceDumper {

    @JvmStatic
    fun main(args: Array<String>) {

        val cachePath = "C:\\Users\\Advo\\Downloads\\cache-oldschool-live-en-b231-2025-07-02-10-45-05-openrs2#2223\\cache"
        val cache = Cache.load(java.nio.file.Path.of(cachePath))
        val objects = GameValHandler.readGameVal(LOCTYPES, cache)
        val npcs = GameValHandler.readGameVal(NPCTYPES, cache)
        val items = GameValHandler.readGameVal(OBJTYPES, cache)
        val filePath = "C:\\Users\\Advo\\Downloads\\OpenRune-FileStore-Server-dump\\wiki\\data\\wiki.xml"
        val wiki = Wiki.load(filePath)

        val pages = wiki.pages
            .asSequence()
            .filter { it.namespace.key == 0 }
            .filter {
                val text = it.revision.text.lowercase()
                text.contains("skilling success chart")
            }

        val toExport = mutableListOf<SkillChance>()
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
                    names.add("items."+items[id].name)
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
                        names.add("npcs."+npcs[id].name)
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
                        names.add("objects."+objects[id].name)
                    }
                }
            }
            if(template.size > 0) {
                toExport.add(SkillChance(
                    title = page.title,
                    names = names,
                    entries = template
                ))

            }
        }

        val gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create()

        File("skillchances.json").writeText(gson.toJson(toExport))
    }
}