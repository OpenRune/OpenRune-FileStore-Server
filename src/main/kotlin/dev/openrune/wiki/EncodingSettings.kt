package dev.openrune.dev.openrune.wiki

data class EncodingSettings(
    val useGameValues : Boolean = false,
    val prettyPrint : Boolean = true,
    val encodeType : FileType = FileType.JSON,
    val linkedIds : Boolean = false
)

enum class FileType {
    JSON,
    TOML
}