plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "OpenRune-FileStore-Server"
include("wiki")
include("filestore-server")
include("common")
include("git-dumper")
