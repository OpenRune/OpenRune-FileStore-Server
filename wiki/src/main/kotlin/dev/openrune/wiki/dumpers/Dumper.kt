package dev.openrune.wiki.dumpers

import dev.openrune.wiki.EncodingSettings
import dev.openrune.wiki.Wiki

interface Dumper {
    fun name(): String
    fun parseItem()
    fun toWrite(encodingSettings : EncodingSettings) : Any
}

fun extractIds(template: Map<String, Any>, keyIndex: Int, ids: MutableList<Pair<Int, Int>>) {
    try {
        val idField = extractValueField("id", template, keyIndex)?.trim()

        if (idField != null) {
            val idsFromField = if (idField.contains(",")) {
                idField.split(",").map { it.trim().toInt() }
            } else {
                listOf(idField.toInt())
            }

            idsFromField.forEach { id ->
                ids.add(Pair(id, keyIndex))
            }
        }
    } catch (e: Exception) {
        if (e !is NumberFormatException && e !is NullPointerException) {
            println("Error processing ID: ${extractValueField("id", template, keyIndex)}")
        }
    }
}

fun extractValueField(key: String,template: Map<String, Any>, index: Int): String? {
    return if (key in template) {
        extractField(key, template).toString()
    } else {
        extractFieldMultiple(key, index + 1, template).toString()
    }
}

fun extractField(key: String, template: Map<String, Any>): String? =
    template[key]?.toString()

fun extractFieldMultiple(key: String, index: Int, template: Map<String, Any>): String? =
    template["${key}$index"]?.toString()
