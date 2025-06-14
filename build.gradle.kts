plugins {
    kotlin("jvm") version "1.9.0"
}

repositories {
    mavenCentral()
    maven("https://raw.githubusercontent.com/OpenRune/hosting/master")
    maven("https://jitpack.io")
}


subprojects {
    apply(plugin = "kotlin")

    group = "dev.openrune"
    version = "0.1"

    repositories {
        mavenCentral()
        maven("https://raw.githubusercontent.com/OpenRune/hosting/master")
        maven("https://jitpack.io")
    }

    dependencies {
        implementation("dev.or2:all:2.0.17")
        implementation("com.google.code.gson:gson:2.12.1")
        implementation("me.tongfei:progressbar:0.9.5")
        implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    }

    java.sourceCompatibility = JavaVersion.VERSION_11

}
