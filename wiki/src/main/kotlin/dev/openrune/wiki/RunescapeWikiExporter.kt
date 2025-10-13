package dev.openrune.wiki

import com.github.michaelbull.logging.InlineLogger
import kotlinx.coroutines.runBlocking
import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarBuilder
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import org.jsoup.Jsoup
import org.jsoup.nodes.FormElement
import java.io.*
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object RunescapeWikiExporter {

    private val logger = InlineLogger()
    private var userAgent = ""

    fun export(exportLoc : File, userAgent : String = "OrWikiDumper-${System.currentTimeMillis()}") {
        exportLoc.mkdirs()

        RunescapeWikiExporter.userAgent = userAgent
        val wiki = "oldschool.runescape.wiki"
        val input = File(exportLoc,"wiki.xml.bz2")
        if (input.exists()) {
            if (!File(exportLoc,"wiki.xml").exists()) {
                decompress(input.path,exportLoc.path)
            }
            return
        }

        val pages = mutableSetOf<String>()
        val pageFile = File(exportLoc,"$wiki.pages")
        if (pageFile.exists()) {
            pages.addAll(pageFile.readLines())
        } else {
            logger.info { "Collecting all Pages this could take a while." }
            val namespaces = listOf("(Main)", "Template", "Help", "Calculator", "Map", "Transcript", "Update", "Module", "User")
            ProgressBar("Collecting namespaces", namespaces.size.toLong()).use { pb ->
                for (namespace in namespaces) {
                    pb.extraMessage = namespace
                    val links = getAllPages(namespace, wiki, hideRedirects = false)
                    pages.addAll(links)
                    pb.step()
                }
            }

            val categories = listOf("Mathematical_templates", "Calculator_templates")
            ProgressBar("Collecting categories", categories.size.toLong()).use { pb ->
                for (category in categories) {
                    pb.extraMessage = category
                    val links = getCategoryLinks("/w/Category:$category", wiki)
                    pages.addAll(links)
                    pb.step()
                }
            }
            pageFile.writeText(pages.joinToString(separator = "\n"))
        }

        exportToFile(
            input,
            pages.toList(),
            wiki,
            currentOnly = true,
            includeTemplates = true,
            batchSize = 5_000
        )
        decompress(input.path,exportLoc.path)
    }

    private fun export(pages: String, wiki: String = "oldschool.runescape.wiki", currentOnly: Boolean = true, includeTemplates: Boolean = false): BufferedInputStream {
        val doc = Jsoup.connect("https://$wiki/w/Special:Export")
            .userAgent(userAgent)
            .header("Accept-Language", "en-US,en;q=0.5")
            .get()
        val form: FormElement = doc.select("form").first { it.attr("action") == "/w/Special:Export" } as FormElement
        val text = form.select("textarea").first { it.attr("name") == "pages" }
        text.`val`(pages)
        val current = form.select("input").first { it.attr("name") == "curonly" }
        val templates = form.select("input").first { it.attr("name") == "templates" }
        val download = form.select("input").first { it.attr("name") == "wpDownload" }

        if (currentOnly) {
            current.attr("checked", "checked")
        } else {
            current.removeAttr("checked")
        }
        if (includeTemplates) {
            templates.attr("checked", "checked")
        } else {
            templates.removeAttr("checked")
        }
        download.removeAttr("checked")
        return form.submit().maxBodySize(0).timeout(0).execute().bodyStream()
    }

    fun exportToFile(file: File, pages: List<String>, wiki: String = "oldschool.runescape.wiki", currentOnly: Boolean = true, includeTemplates: Boolean = false, batchSize: Int = 5_000) {
        val parent = File(file.parentFile,"pages")
        parent.mkdirs()
        val files = runBlocking {
            val chunks = pages.chunked(batchSize)
            val tempFiles = mutableListOf<File>()

            ProgressBar("Downloading", chunks.size.toLong()).use { pb ->
                chunks.forEachIndexed { index, list ->
                    val temp = parent.resolve("${file.nameWithoutExtension}_temp_${index}.xml")

                    if (!temp.exists()) {
                        temp.writeBytes(
                            export(
                                list.joinToString(separator = "\n"),
                                wiki,
                                currentOnly,
                                includeTemplates
                            ).readAllBytes()
                        )
                    }

                    pb.step()
                    tempFiles.add(temp)
                }

                tempFiles
            }
            tempFiles
        }

        val pb = ProgressBar("Combing files ", files.size.toLong())
        var first = true
        val compressStream = BZip2CompressorOutputStream(BufferedOutputStream(file.outputStream()))
        for (temp in files) {
            val text = temp.readText()
            val start = text.indexOf("<page>")
            val end = text.lastIndexOf("</page>")
            if (start == -1 || end == -1) {
                continue
            }
            if (first) {
                compressStream.write(text.substring(0, end + 7).toByteArray())
                first = false
            } else {
                compressStream.write("\n  ".toByteArray())
                compressStream.write(text.substring(start, end + 7).toByteArray())
            }
            pb.step()
        }
        compressStream.write("\n</mediawiki>\n".toByteArray())
        compressStream.close()
        pb.close()

    }

    fun getCategoryLinks(url: String, wiki: String = "oldschool.runescape.wiki", list: MutableList<String> = mutableListOf()): List<String> {
        val connect = Jsoup.connect("https://$wiki/$url").userAgent(userAgent)
            .header("Accept-Language", "en-US,en;q=0.5")
        val response = connect.execute()
        if (response.statusCode() != 200) {
            println("Page not found: $wiki $url")
            return list
        }
        val doc = response.parse()
        val element = doc.select("#mw-pages")
        for (ele in element.select("div[class$=mw-category-group]")) {
            for (item in ele.select("ul li a")) {
                val link = item.attr("href")
                list.add(URLDecoder.decode(link, StandardCharsets.UTF_8.name()))
            }
        }
        val nextPage = element.select("a:contains(next page)").attr("href")
        if (nextPage.isNotBlank()) {
            getCategoryLinks(nextPage, wiki, list)
        }
        return list
    }

    fun getCategories(wiki: String = "oldschool.runescape.wiki", url: String = "/w/Special:Categories?limit=500", list: MutableList<String> = mutableListOf()): List<String> {
        val doc = Jsoup.connect("https://${wiki}$url").userAgent(userAgent)
            .header("Accept-Language", "en-US,en;q=0.5")
            .get()

        val elements = doc.select("div[class$=mw-spcontent] ul li")
        for (ele in elements) {
            val link = ele.select("a")
            list.add(link.attr("title"))
        }
        val next = doc.select("a[class$=mw-nextlink]").firstOrNull()
        if (next != null) {
            getCategories(wiki, next.attr("href"), list)
        }
        return list
    }

    fun getCategoriesLinks(wiki: String = "oldschool.runescape.wiki", url: String = "/w/Special:Categories?limit=500", list: MutableList<String> = mutableListOf()): List<String> {
        val doc = Jsoup.connect("https://${wiki}$url").get()
        val elements = doc.select("div[class$=mw-spcontent] ul li")
        for (ele in elements) {
            val link = ele.select("a")
            list.add(link.attr("href"))
        }
        val next = doc.select("a[class$=mw-nextlink]").firstOrNull()
        if (next != null) {
            getCategories(wiki, next.attr("href"), list)
        }
        return list
    }

    fun getAllPages(namespace: String, wiki: String = "oldschool.runescape.wiki", hideRedirects: Boolean = true): List<String> {
        val namespaces = getNamespaces(wiki)
        val namespaceId = namespaces[namespace]!!
        val url = "/w/Special:AllPages?from=&to=&namespace=$namespaceId${if (hideRedirects) "&hideredirects=1" else ""}"
        return getAllPages(mutableListOf(), wiki, url)
    }

    private fun getAllPages(list: MutableList<String>, wiki: String, url: String): List<String> {
        val doc = Jsoup.connect("https://${wiki}$url")
            .userAgent(userAgent)
            .header("Accept-Language", "en-US,en;q=0.5")
            .get()

        val elements = doc.select("div[class$=mw-allpages-body] a")
        for (ele in elements) {
            list.add(ele.attr("title"))
        }

        val next = doc.select("div[class$=mw-allpages-nav] a")
            .firstOrNull { it.text().startsWith("Next page") }

        if (next != null) {
            Thread.sleep(500)
            getAllPages(list, wiki, next.attr("href"))
        }

        return list
    }

    private fun getNamespaces(wiki: String): Map<String, Int> {
        val doc = Jsoup.connect("https://$wiki/w/Special:AllPages")
            .userAgent(userAgent)
            .header("Accept-Language", "en-US,en;q=0.5")
            .get()

        val options = doc.select("div[class$=mw-widget-namespaceInputWidget] option")
        return options.associate { it.text() to it.attr("value").toInt() }
    }

    fun decompress(xmlBz2FilePath: String, outputDir: String) {
        val file = File(xmlBz2FilePath)

        val outputFile = File(outputDir, file.nameWithoutExtension)
        if (outputFile.exists()) {
            outputFile.delete()
        }

        val rawInputStream = FileInputStream(file)

        val bzipStream = BZip2CompressorInputStream(rawInputStream)


        val outputStream = FileOutputStream(outputFile)
        val progressBar = ProgressBarBuilder()
            .setTaskName("Writing Wiki Xml output")
            .setInitialMax(600L * 1024 * 1024)
            .build()

        val progressOutputStream = ProgressOutputStream(outputStream, progressBar)

        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (bzipStream.read(buffer).also { bytesRead = it } != -1) {
            progressOutputStream.write(buffer, 0, bytesRead)
        }

        bzipStream.close()
        progressOutputStream.close()
    }

    class ProgressOutputStream(
        out: OutputStream,
        private val progressBar: ProgressBar
    ) : FilterOutputStream(out) {

        override fun write(b: Int) {
            super.write(b)
            progressBar.stepBy(1)
        }

        override fun write(b: ByteArray, off: Int, len: Int) {
            super.write(b, off, len)
            progressBar.stepBy(len.toLong())
        }

        override fun close() {
            super.close()
            progressBar.close()
        }
    }


}