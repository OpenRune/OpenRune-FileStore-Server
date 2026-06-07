package dev.openrune.wiki

import java.io.File

/**
 * In-memory index over a local MediaWiki XML dump.
 *
 * Loads page wikitext from [wiki.xml] or split [pages/wiki.xml_temp_*.xml] shards,
 * follows redirects, and builds a category membership index from page wikitext.
 */
class WikiDumpStore private constructor(
    val pagesByTitle: Map<String, WikiDumpPage>,
    private val categoryMembers: Map<String, List<String>>,
    private val infoboxMonsterTitles: List<String>,
) {
    data class WikiDumpPage(
        val title: String,
        val namespace: Int,
        val redirectTitle: String?,
        val text: String,
    )

    fun rawPageSource(title: String): String {
        val page = resolvePage(title)
            ?: throw IllegalStateException("The page '$title' does not exist.")
        if (page.text.isBlank()) {
            throw IllegalStateException("No wikitext source available for '$title'.")
        }
        return page.text
    }

    fun listCategoryMemberTitles(
        category: String,
        offset: Int = 0,
        limit: Int = 500,
    ): List<String> {
        val key = normalizeCategory(category)
        val members = categoryMembers[key] ?: return emptyList()
        if (offset >= members.size) {
            return emptyList()
        }
        return members.drop(offset).take(limit.coerceAtLeast(1))
    }

    fun categoryMemberCount(category: String): Int =
        categoryMembers[normalizeCategory(category)]?.size ?: 0

    fun listInfoboxMonsterTitles(
        offset: Int = 0,
        limit: Int = 500,
    ): List<String> {
        if (offset >= infoboxMonsterTitles.size) {
            return emptyList()
        }
        return infoboxMonsterTitles.drop(offset).take(limit.coerceAtLeast(1))
    }

    val infoboxMonsterCount: Int get() = infoboxMonsterTitles.size

    val pageCount: Int get() = pagesByTitle.size

    val categoryCount: Int get() = categoryMembers.size

    /** Main-namespace pages (ns=0) that are not redirects. */
    fun mainNamespacePages(): Sequence<WikiDumpPage> =
        pagesByTitle.values
            .asSequence()
            .filter { page -> page.namespace == 0 && page.redirectTitle == null }

    private fun resolvePage(title: String): WikiDumpPage? {
        var current = findPage(title) ?: return null
        val visited = mutableSetOf<String>()
        while (true) {
            val redirect = current.redirectTitle ?: return current
            if (!visited.add(current.title)) {
                return current
            }
            current = findPage(redirect) ?: return current
        }
    }

    private fun findPage(title: String): WikiDumpPage? {
        for (candidate in titleCandidates(title)) {
            pagesByTitle[candidate]?.let { return it }
            pagesByTitle.entries.firstOrNull { it.key.equals(candidate, ignoreCase = true) }?.value?.let { return it }
        }
        return null
    }

    private fun titleCandidates(title: String): List<String> {
        val trimmed = title.trim()
        val withSpaces = trimmed.replace('_', ' ')
        val withUnderscores = trimmed.replace(' ', '_')
        return listOf(trimmed, withSpaces, withUnderscores).distinct()
    }

    companion object {
        private val categoryTag =
            Regex("""\[\[Category:([^|\]#]+)""", RegexOption.IGNORE_CASE)

        private val infoboxMonsterTag =
            Regex("""\{\{Infobox Monster\b""", RegexOption.IGNORE_CASE)

        private val infoboxRemovedIdField =
            Regex("""(?im)^\s*\|id(\d*)\s*=\s*(.+?)\s*$""")

        private fun hasNonNumericNpcId(wikitext: String): Boolean {
            val start = infoboxMonsterTag.find(wikitext)?.range?.first ?: return false
            val end = wikitext.indexOf("}}", start).takeIf { it >= 0 } ?: return false
            val infobox = wikitext.substring(start, end)
            val values = infoboxRemovedIdField.findAll(infobox).map { it.groupValues[2].trim() }.toList()
            if (values.isEmpty()) {
                return false
            }
            val tokens =
                values
                    .flatMap { value -> value.split(',').map { it.trim() }.filter { it.isNotEmpty() } }
            if (tokens.isEmpty()) {
                return false
            }
            return tokens.none { it.toIntOrNull() != null }
        }

        fun load(dumpDirectory: File): WikiDumpStore {
            require(dumpDirectory.isDirectory) { "Wiki dump directory does not exist: $dumpDirectory" }

            val xmlFiles = resolveXmlFiles(dumpDirectory)
            require(xmlFiles.isNotEmpty()) {
                "No wiki XML files found under $dumpDirectory (expected wiki.xml or pages/wiki.xml_temp_*.xml)"
            }

            val pagesByTitle = linkedMapOf<String, WikiDumpPage>()
            for (file in xmlFiles) {
                mergeWikiFile(file, pagesByTitle)
            }

            val categoryMembers = buildCategoryIndex(pagesByTitle.values)
            val infoboxMonsterTitles = buildInfoboxMonsterIndex(pagesByTitle.values)
            return WikiDumpStore(pagesByTitle, categoryMembers, infoboxMonsterTitles)
        }

        private fun mergeWikiFile(file: File, pagesByTitle: MutableMap<String, WikiDumpPage>) {
            val wiki = Wiki.load(file.absolutePath)
            for (page in wiki.pages) {
                pagesByTitle[page.title] =
                    WikiDumpPage(
                        title = page.title,
                        namespace = page.namespace.key,
                        redirectTitle = page.redirect.takeIf { it.isNotBlank() },
                        text = page.revision.text,
                    )
            }
        }

        private fun resolveXmlFiles(dumpDirectory: File): List<File> {
            val pagesDir = File(dumpDirectory, "pages")
            val shards =
                pagesDir
                    .listFiles { file -> file.isFile && file.name.startsWith("wiki.xml_temp_") && file.name.endsWith(".xml") }
                    ?.sortedBy { it.name }
                    .orEmpty()
                    .toList()

            if (shards.isNotEmpty()) {
                return shards
            }

            val single = File(dumpDirectory, "wiki.xml")
            return if (single.isFile) listOf(single) else emptyList()
        }

        private fun buildCategoryIndex(pages: Collection<WikiDumpPage>): Map<String, List<String>> {
            val members = linkedMapOf<String, MutableList<String>>()
            for (page in pages) {
                if (page.namespace != 0 || page.redirectTitle != null) {
                    continue
                }
                for (match in categoryTag.findAll(page.text)) {
                    val category = match.groupValues[1].trim()
                    if (category.isBlank()) {
                        continue
                    }
                    members.getOrPut(category) { mutableListOf() }.add(page.title)
                }
            }
            return members.mapValues { (_, titles) -> titles.distinct().sorted() }
        }

        private fun buildInfoboxMonsterIndex(pages: Collection<WikiDumpPage>): List<String> =
            pages
                .asSequence()
                .filter { page -> page.namespace == 0 && page.redirectTitle == null }
                .filter { page -> infoboxMonsterTag.containsMatchIn(page.text) }
                .filter { page -> !hasNonNumericNpcId(page.text) }
                .map { page -> page.title }
                .distinct()
                .sorted()
                .toList()

        private fun normalizeCategory(category: String): String =
            category.removePrefix("Category:").trim()
    }
}
