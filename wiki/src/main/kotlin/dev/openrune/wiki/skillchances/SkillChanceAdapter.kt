package dev.openrune.wiki.skillchances

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class SkillChanceAdapter : TypeAdapter<SkillChance>() {
    override fun write(out: JsonWriter, value: SkillChance) {
        out.beginObject()
        out.name("title").value(value.title)

        if (value.itemIds.isNotEmpty()) {
            out.name("itemIds")
            out.beginArray()
            value.itemIds.forEach { out.value(it) }
            out.endArray()
        }

        if (value.objectIds.isNotEmpty()) {
            out.name("objectIds")
            out.beginArray()
            value.objectIds.forEach { out.value(it) }
            out.endArray()
        }

        if (value.npcIds.isNotEmpty()) {
            out.name("npcIds")
            out.beginArray()
            value.npcIds.forEach { out.value(it) }
            out.endArray()
        }

        out.name("entries")
        out.beginArray()
        for (entry in value.entries) {
            out.beginObject()
            for ((key, v) in entry) {
                out.name(key)
                when (v) {
                    is Number -> out.value(v)
                    is Boolean -> out.value(v)
                    is String -> out.value(v)
                    else -> out.nullValue()
                }
            }
            out.endObject()
        }
        out.endArray()

        out.endObject()
    }

    override fun read(reader: JsonReader): SkillChance {
        throw UnsupportedOperationException("Deserialization is not supported")
    }
}
