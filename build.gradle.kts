plugins {
    kotlin("jvm") version "1.9.0"
}


subprojects {
    apply(plugin = "kotlin")

    group = "dev.openrune"
    version = "0.1"

    repositories {
        mavenCentral()
        maven("https://raw.githubusercontent.com/OpenRune/hosting/master")
    }

    dependencies {
        implementation("dev.or2:all:2.0.17")
        implementation("com.google.code.gson:gson:2.12.1")
        implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    }

    java.sourceCompatibility = JavaVersion.VERSION_11

}
