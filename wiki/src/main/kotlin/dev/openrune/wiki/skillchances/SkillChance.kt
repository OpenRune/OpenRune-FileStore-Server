package dev.openrune.wiki.skillchances

data class SkillChance(val title: String, val itemIds: List<String> = mutableListOf<String>(), val objectIds: List<String> = mutableListOf<String>(), val npcIds: List<String> = mutableListOf<String>(), val entries: List<Map<String, Any>>)
