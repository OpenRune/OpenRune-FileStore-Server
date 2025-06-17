package dev.openrune.wiki

import dev.openrune.OsrsCacheProvider
import dev.openrune.cache.CacheManager
import dev.openrune.cache.tools.GameType
import dev.openrune.cache.tools.OpenRS2
import dev.openrune.cache.util.stringToTimestamp
import dev.openrune.cache.util.toEchochUTC
import dev.openrune.filesystem.Cache
import dev.openrune.server.ServerCacheManager
import dev.openrune.server.ServerCacheManager.buildServerCacheConfig
import dev.openrune.wiki.RunescapeWikiExporter
import dev.openrune.wiki.WikiDumper
import dev.openrune.wiki.dumpers.Items
import dev.openrune.wiki.dumpers.Items.ITEM_LOCATION
import dev.openrune.wiki.dumpers.Npcs
import dev.openrune.wiki.dumpers.Npcs.NPC_LOCATION
import dev.openrune.wiki.dumpers.Objects
import dev.openrune.wiki.dumpers.Objects.OBJECTS_LOCATION
import dev.or2.wiki.BuildConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.TextProgressMonitor
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

var SAVE_LOCATION = Path.of(System.getProperty("user.home"), "Desktop", "dump")
var WIKI_LOCATION = SAVE_LOCATION.resolve("wiki")
var CACHE_LOCATION = SAVE_LOCATION.resolve("cache")
var DATA_LOCATION = SAVE_LOCATION.resolve("data")

var token = ""
private const val OWNER = "OpenRune"
private const val REPO_NAME = "OpenRune-FileStore-Server"
private const val BRANCH = "dump"

private const val DUMP_WIKI_PAGES = true


@OptIn(ExperimentalPathApi::class)
fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Error: Token argument is required.")
        println("Usage: program <token> [saveLocation]")
        return
    }

    token = args[0]

    val saveLocationArg = if (args.size > 1) args[1] else "./gitDump"
    SAVE_LOCATION = Path.of(saveLocationArg).toAbsolutePath()
    WIKI_LOCATION = SAVE_LOCATION.resolve("wiki")
    CACHE_LOCATION = SAVE_LOCATION.resolve("cache")
    DATA_LOCATION = SAVE_LOCATION.resolve("data")

    OpenRS2.loadCaches()

    val localRev = BuildConfig.LAST_UPDATED_REVISION
    val storedTimestamp = BuildConfig.LAST_UPDATED_TIME

    val latest = OpenRS2.getLatest(OpenRS2.allCaches, GameType.OLDSCHOOL)
    val currentRevision = latest.builds.first().major
    val currentTimeStamp = latest.timestamp.stringToTimestamp().toEchochUTC()

    if (localRev != currentRevision || storedTimestamp != currentTimeStamp) {
        val tmpDir = SAVE_LOCATION.toFile()
        logger.info { "Clone Repo" }

        val credentials = UsernamePasswordCredentialsProvider(token, "")

        val git = if (!tmpDir.exists() || !File(tmpDir, ".git").exists()) {
            Git.cloneRepository()
                .setURI("https://github.com/$OWNER/$REPO_NAME.git")
                .setBranch(BRANCH)
                .setCredentialsProvider(credentials)
                .setDirectory(tmpDir)
                .call()
        } else {
            val git = Git.open(tmpDir)
            git.pull().setCredentialsProvider(credentials).call()
            git
        }

        if (DUMP_WIKI_PAGES) {
            WIKI_LOCATION.deleteRecursively()
            RunescapeWikiExporter.export(WIKI_LOCATION.toFile())
            pushWikiFiles(tmpDir,currentRevision,git, latest.timestamp)
        }

        WikiDumper.getBaseLocation = SAVE_LOCATION.toFile()
        WikiDumper.rev = currentRevision
        WikiDumper.wikiLocation = WIKI_LOCATION.resolve("wiki.xml").toFile()

        WikiDumper.setup()


        val cacheProvider = OsrsCacheProvider(Cache.load(CACHE_LOCATION, false), currentRevision)
        CacheManager.init(cacheProvider)


        Items.init()
        Objects.init()
        Npcs.init()

        ServerCacheManager.init(buildServerCacheConfig {
            dataStore =  cacheProvider
            addItemPath(ITEM_LOCATION.resolve("items-wiki-only.json"))
            addObjectPath(OBJECTS_LOCATION.resolve("object-examines.csv"))
            addNpcPath(NPC_LOCATION.resolve("npcs-wiki-only.json"))
        })

        Items.writeServerData()
        Objects.writeServerData()
        Npcs.writeServerData()

        logger.info { "Pushing..." }

        pushDumpFiles(tmpDir,currentRevision,git, latest.timestamp)

        logger.info { "Updated To $currentRevision : $currentTimeStamp" }

    } else {
        logger.info { "No updates found." }
        exitProcess(0)
    }
}

private fun pushDumpFiles(
    tmpDir: File,
    currentRevision: Int,
    git: Git,
    timeStamp: String
) {
    val repoDir = tmpDir
    val gitIgnoreHelper = GitIgnoreHelper(repoDir)

    repoDir.walkTopDown()
        .filter { it.isFile }
        .map { it.relativeTo(repoDir) }
        .filter { !gitIgnoreHelper.isIgnored(it.path.replace(File.separatorChar, '/'), false) }
        .forEach {
            git.add().addFilepattern(it.path.replace(File.separatorChar, '/')).call()
        }

    logger.info { "Committing git pages changes." }

    git.commit()
        .setAllowEmpty(true)
        .setMessage("Updated to rev $currentRevision - $timeStamp")
        .call()

    val status = git.status().call()
    logger.info {
        buildString {
            appendLine("Added: ${status.added}")
            appendLine("Changed: ${status.changed}")
            appendLine("Untracked: ${status.untracked}")
            appendLine("Removed: ${status.removed}")
        }
    }

    logger.info { "Pushing git pages changes." }
    git.push()
        .setCredentialsProvider(UsernamePasswordCredentialsProvider(token, ""))
        .setProgressMonitor(TextProgressMonitor())
        .call()
}

private fun pushWikiFiles(tmpDir: File, currentRevision: Int, git: Git, timeStamp: String) {
    val gitIgnoreHelper = GitIgnoreHelper(tmpDir)

    tmpDir.walkTopDown()
        .filter { it.isFile }
        .map { it.relativeTo(tmpDir) }
        .filter { !gitIgnoreHelper.isIgnored(it.path.replace(File.separatorChar, '/'), false) }
        .forEach {
            git.add().addFilepattern(it.path.replace(File.separatorChar, '/')).call()
        }

    logger.info { "Committing git pages changes." }

    git.commit()
        .setAllowEmpty(true)
        .setMessage("Updated wiki pages to rev $currentRevision - $timeStamp")
        .call()

    val status = git.status().call()
    logger.info {
        buildString {
            appendLine("Added: ${status.added}")
            appendLine("Changed: ${status.changed}")
            appendLine("Untracked: ${status.untracked}")
            appendLine("Removed: ${status.removed}")
        }
    }

    logger.info { "Pushing git pages changes." }
    git.push()
        .setCredentialsProvider(UsernamePasswordCredentialsProvider(token, ""))
        .setProgressMonitor(TextProgressMonitor())
        .call()
}