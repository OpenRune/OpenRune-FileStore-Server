package dev.openrune.wiki.dumpers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dev.openrune.server.ServerCacheManager
import java.io.File
import java.io.IOException

class ItemRequirementsManager {

    private var requirements: MutableMap<Int, MutableMap<String, Int>> = emptyMap<Int, MutableMap<String, Int>>().toMutableMap()

    fun load() {
        val inputStream = javaClass.getResourceAsStream("/items-skill-requirements.json") ?: throw IllegalStateException("item_requirements.json not found in resources")
        val reader = inputStream.bufferedReader()
        val type = object : TypeToken<Map<Int, Map<String, Int>>>() {}.type
        requirements = Gson().fromJson(reader, type)
    }

    fun getRequirementsForItem(itemId: Int): Map<String, Int>? {
        return requirements[itemId]
    }

    fun removeItemsByIds() {

        val equipmentWithIds = emptyList<Int>().toMutableList()

        val skillReq = ItemRequirementsManager()
        skillReq.load()

        ServerCacheManager.getItems().forEach {
            if (it.value.equipment != null) {
                if (it.value.equipment!!.requirements.isNotEmpty() &&
                    skillReq.getRequirementsForItem(it.key) != null) {
                    val skillReqNames = skillReq.getRequirementsForItem(it.key)
                        ?.mapNotNull {
                            if (it.value > 1) it.key.lowercase() else null
                        }
                    if (skillReqNames?.isEmpty() != true) {
                        val skillReqNamesCache = it.value.equipment!!.requirements.map { it.key.lowercase() }
                        println("========== ${it.key}  ===========")
                        println("OSRESBOX: " + skillReqNames)
                        println("Cache: " + skillReqNamesCache)
                        equipmentWithIds.add(it.key)
                    }
                }
            }
        }


        equipmentWithIds.forEach { itemId ->
            requirements.remove(itemId)
        }


        val itemsToRemove = mutableListOf<Int>()

        requirements.forEach { (item, req) ->
            req.entries.removeIf { it.value == 1 }

            if (req.isEmpty()) {
                itemsToRemove.add(item)
            }
        }

        itemsToRemove.forEach { item ->
            requirements.remove(item)
        }

        try {
            File("items-skill-requirements_redump.json").writeText(GsonBuilder().setPrettyPrinting().create().toJson(requirements))
        } catch (e: IOException) {
            throw IllegalStateException("Failed to save updated requirements to file", e)
        }
    }

}

fun  main() {
    val items = ItemRequirementsManager()
    items.load()
    items.removeItemsByIds()
}