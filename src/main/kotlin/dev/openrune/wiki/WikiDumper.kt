package dev.openrune.dev.openrune.wiki

import com.google.gson.GsonBuilder
import com.moandjiezana.toml.TomlWriter
import dev.openrune.dev.openrune.wiki.dumpers.impl.InfoBoxItem
import dev.openrune.dev.openrune.wiki.dumpers.impl.InfoBoxItemSerializer
import dev.openrune.dev.openrune.wiki.dumpers.impl.Items

import java.io.File

object WikiDumper {

    val wiki = Wiki.load("E:\\RSPS\\OpenRune\\OpenRune-FileStore-Server\\oldschool-runescape-wiki-2025-04-10.xml")

    fun init(encodingSettings: EncodingSettings = EncodingSettings()) {
        val items = Items()
        items.parseItem(wiki)
        writeData(encodingSettings,items.toWrite(encodingSettings), items.name())
    }
}

fun writeData(encodingSettings: EncodingSettings, data : Any, name :String) {
    when (encodingSettings.encodeType) {
        FileType.JSON -> {
            val gsonBuilder = GsonBuilder().apply {
                if (encodingSettings.prettyPrint) {
                    setPrettyPrinting()
                }
                registerTypeAdapter(InfoBoxItem::class.java, InfoBoxItemSerializer())
            }
            File("${name}.json").writeText(gsonBuilder.create().toJson(data))
        }
        FileType.TOML -> {
            val tomlWriter = TomlWriter()
            tomlWriter.write(data, File("${name}.toml"))
        }
        else -> { }
    }
}

fun main(args: Array<String>) {
    WikiDumper.init(
        EncodingSettings(encodeType = FileType.JSON, prettyPrint = true, linkedIds = false)
    )
    WikiDumper.init(EncodingSettings(encodeType = FileType.TOML, prettyPrint = true, linkedIds = false))
}
