package dev.openrune.wiki.skillchances

data class SkillChance(val title: String, val names: List<String> = mutableListOf(), val entries: List<Map<String, Any>>)
