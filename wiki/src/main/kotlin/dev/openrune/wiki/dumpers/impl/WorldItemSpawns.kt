package dev.openrune.wiki.dumpers.impl

import com.github.michaelbull.logging.InlineLogger
import dev.openrune.wiki.EncodingSettings
import dev.openrune.wiki.Wiki
import dev.openrune.wiki.WikiDumper
import dev.openrune.wiki.dumpers.Dumper
import dev.openrune.wiki.dumpers.extractIds
import dev.openrune.wiki.dumpers.extractValueField
import me.tongfei.progressbar.ProgressBarBuilder

data class ItemSpawnEntry(
    val qty : Int,
    val respawn : Int,
    val location : List<Int>
)

class WorldItemSpawns : Dumper {

    private val logger = InlineLogger()

    val chunkRegex = Regex("""\d+,\d+[^,]*(?:,[^,]+)*""")
    val xyRegex = Regex("""(\d+),(\d+)""")
    val qtyRegex = Regex("""qty:(\d+)""")
    val planeRegex = Regex("""plane:(\d+)""")
    val respawnRegex = Regex("""respawn:(\d+)""")


    override fun name() = "itemSpawns"
    var itemSpawns = mutableMapOf<Int, List<ItemSpawnEntry>>()

    override fun parseItem() {
        val parsedItems = mutableMapOf<Int, List<ItemSpawnEntry>>()

        val pages = WikiDumper.wiki.pages.asSequence()
            .filter { it.namespace.key == 0 }
            .filter { it.revision.text.contains("ItemSpawnLine", ignoreCase = true) }

        val pb = ProgressBarBuilder().setInitialMax((pages.count()).toLong()).setTaskName("Dumping Item Spawns").build()

        pages.forEach { page ->
            val templatesInfoBox = page.getTemplateMaps("infobox item")
            if (templatesInfoBox.isEmpty()) return@forEach
            val template = templatesInfoBox.first()

            val templatesSpawns = page.getTemplateMaps("itemspawnline")

            val idKeys = template.keys.filter { it == "id" || it.matches(Regex("id\\d+")) }
            val idCount = idKeys.size


            val ids = mutableListOf<Pair<Int, Int>>()
            for (keyIndex in 1..idCount) {
                extractIds(template, keyIndex, ids)
            }
            val id = ids.first().first
            val respawnTime = extractValueField("respawn", template, 0)?.toIntOrNull() ?: 60

            val spawnList : MutableList<ItemSpawnEntry> = emptyList<ItemSpawnEntry>().toMutableList()

            templatesSpawns.forEach { spawn ->
                var planeFound = 0
                if (spawn.contains("plane")) {
                    planeFound = extractValueField("plane", spawn, 0)?.toIntOrNull() ?: 0
                }


                val loc = spawn[""]

                val chunks = chunkRegex.findAll(loc.toString())

                for (chunkMatch in chunks) {
                    val chunk = chunkMatch.value

                    val xy = xyRegex.find(chunk)!!
                    val x = xy.groupValues[1].toInt()
                    val y = xy.groupValues[2].toInt()

                    val qty = qtyRegex.find(chunk)?.groupValues?.get(1)?.toInt() ?: 1
                    val plane = planeRegex.find(chunk)?.groupValues?.get(1)?.toInt() ?: planeFound
                    val respawn = respawnRegex.find(chunk)?.groupValues?.get(1)?.toInt()?: respawnTime
                    val itemSpawnEntry = ItemSpawnEntry(qty,respawn,listOf(x,y,plane))
                    spawnList.add(itemSpawnEntry)
                }
            }


            parsedItems[id] = spawnList

            pb.step()
        }
        pb.close()
        this.itemSpawns = parsedItems
    }

    override fun toWrite(encodingSettings: EncodingSettings): Any {
        return itemSpawns.toSortedMap()
    }

}