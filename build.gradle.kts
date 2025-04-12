plugins {
    kotlin("jvm") version "2.0.0"
}

group = "dev.openrune"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.scijava.org/content/repositories/public/")
    maven("https://raw.githubusercontent.com/OpenRune/hosting/master")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("dev.or2:all:2.0.11")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("org.sweble.wikitext:swc-engine:3.1.9")
    implementation("org.apache.commons:commons-compress:1.21")
    // Correcting the coroutines dependency from runtimeOnly to implementation
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("cc.ekblad:4koma:1.2.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.3")
    // For testing dependencies (e.g., JUnit)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.12.1")
    implementation("com.moandjiezana.toml:toml4j:0.7.2")
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("org.openjdk.jol:jol-core:0.16")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    implementation("me.tongfei:progressbar:0.9.5")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}