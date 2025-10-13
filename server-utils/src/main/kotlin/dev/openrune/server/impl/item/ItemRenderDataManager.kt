package dev.openrune.server.impl.item

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

data class WeaponTypeRenderDataFull(
    val name: String,
    var associatedItems : List<Int>,
    val walkAnim: Int,
    val runAnim: Int,
    val readyAnim: Int,
    val turnAnim: Int,
    val walkAnimBack: Int,
    val walkAnimLeft: Int,
    val walkAnimRight: Int,
    val accurateAnim: Int,
    val accurateSound: Int,
    val aggressiveAnim: Int,
    val aggressiveSound: Int,
    val controlledAnim: Int,
    val controlledSound: Int,
    val defensiveAnim: Int,
    val defensiveSound: Int,
    val blockAnim: Int
)

data class WeaponTypeRenderData(
    val walkAnim: Int,
    val runAnim: Int,
    val readyAnim: Int,
    val turnAnim: Int,
    val walkAnimBack: Int,
    val walkAnimLeft: Int,
    val walkAnimRight: Int,
    val accurateAnim: Int,
    val accurateSound: Int,
    val aggressiveAnim: Int,
    val aggressiveSound: Int,
    val controlledAnim: Int,
    val controlledSound: Int,
    val defensiveAnim: Int,
    val defensiveSound: Int,
    val blockAnim: Int
)

fun WeaponTypeRenderDataFull.toServer(): WeaponTypeRenderData {
    return WeaponTypeRenderData(
        walkAnim = walkAnim,
        runAnim = runAnim,
        readyAnim = readyAnim,
        turnAnim = turnAnim,
        walkAnimBack = walkAnimBack,
        walkAnimLeft = walkAnimLeft,
        walkAnimRight = walkAnimRight,
        accurateAnim = accurateAnim,
        accurateSound = accurateSound,
        aggressiveAnim = aggressiveAnim,
        aggressiveSound = aggressiveSound,
        controlledAnim = controlledAnim,
        controlledSound = controlledSound,
        defensiveAnim = defensiveAnim,
        defensiveSound = defensiveSound,
        blockAnim = blockAnim
    )
}


object ItemRenderDataManager {

    private val weaponTypeRenderDataFullMap: MutableMap<Int, WeaponTypeRenderDataFull> = mutableMapOf()

    fun init(input : InputStream = javaClass.classLoader.getResourceAsStream("itemRenderData.json")) {
        val gson = Gson()

        val reader = BufferedReader(InputStreamReader(input))

        val mapType = object : TypeToken<Map<Int, WeaponTypeRenderDataFull>>() {}.type
        val deserializedMap: Map<Int, WeaponTypeRenderDataFull> = gson.fromJson(reader.readText(), mapType)
        weaponTypeRenderDataFullMap.clear()
        weaponTypeRenderDataFullMap.putAll(deserializedMap)
    }

    fun clear() {
        weaponTypeRenderDataFullMap.clear()
    }

    fun getItemRenderAnimationByItem(id: Int): WeaponTypeRenderDataFull? {
        return weaponTypeRenderDataFullMap.values.find { id in it.associatedItems }
    }

    fun getItemRenderAnimationById(id: Int): WeaponTypeRenderDataFull? {
        return weaponTypeRenderDataFullMap[id]
    }

    fun getAllItemRenderAnimations(): List<WeaponTypeRenderDataFull> {
        return weaponTypeRenderDataFullMap.values.toList()
    }

    fun addOrUpdateItemRenderAnimation(id: Int, itemRenderAnimation: WeaponTypeRenderDataFull) {
        weaponTypeRenderDataFullMap[id] = itemRenderAnimation
    }
}