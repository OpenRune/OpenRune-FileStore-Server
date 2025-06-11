package dev.openrune.wiki

import org.eclipse.jgit.ignore.IgnoreNode
import java.io.File

class GitIgnoreHelper(private val repoDir: File) {
    private val ignoreNode = IgnoreNode()

    init {
        val gitIgnoreFile = File(repoDir, ".gitignore")
        if (gitIgnoreFile.exists()) {
            gitIgnoreFile.inputStream().use { reader ->
                ignoreNode.parse(reader)
            }
        }
    }

    /**
     * Checks if a given file path relative to repoDir should be ignored based on .gitignore rules.
     *
     * @param relativePath the path relative to the root of the repo (e.g. "src/main/MyFile.kt")
     * @param isDirectory true if the path is a directory
     * @return true if the file/directory should be ignored
     */
    fun isIgnored(relativePath: String, isDirectory: Boolean): Boolean {
        return ignoreNode.isIgnored(relativePath, isDirectory) == IgnoreNode.MatchResult.IGNORED
    }
}