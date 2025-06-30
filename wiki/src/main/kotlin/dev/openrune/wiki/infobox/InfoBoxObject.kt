package dev.openrune.wiki.infobox

import java.nio.file.Path
import kotlin.collections.set
import kotlin.io.path.readText

data class InfoBoxObject(
    val examine: String
) {
    override fun hashCode(): Int {
        return (examine.hashCode())
    }

    companion object {

        fun load(objectExamines: Path): Map<Int, InfoBoxObject> {
            val flatMap = mutableMapOf<Int, InfoBoxObject>()

            objectExamines.readText().lines().forEach { line ->
                val parts = line.split(',')
                if (parts.size < 2) return@forEach

                val id = parts[0].toIntOrNull() ?: return@forEach
                val description = parts[1]

                flatMap[id] = InfoBoxObject(description)
            }

            return flatMap
        }
    }

}