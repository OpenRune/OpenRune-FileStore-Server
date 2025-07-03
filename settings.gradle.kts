plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "OpenRune-FileStore-Server"
include("wiki")
include("wiki-model")

include("filestore-server-osrs")
//include("git-dumper")
findProject(":filestore-server:filestore-server-osrs")?.name = "filestore-server-osrs"
