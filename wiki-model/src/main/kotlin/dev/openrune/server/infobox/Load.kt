package dev.openrune.server.infobox

import java.nio.file.Files
import java.nio.file.Path

object Load {
    fun getDefaultResourceTempFile(fileName: String): Path {
        val inputStream = javaClass.getResourceAsStream("/$fileName")
            ?: error("Missing default resource: /$fileName")

        val tempFile = Files.createTempFile("default_resource_", "_$fileName")
        inputStream.use { input ->
            Files.newOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        tempFile.toFile().deleteOnExit()
        return tempFile
    }
}