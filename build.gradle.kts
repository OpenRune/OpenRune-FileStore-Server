import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("maven-publish")
}

val buildDirectory = "E:\\RSPS\\OpenRune\\hosting"
val buildNumber = "0.7"

group = "dev.or2"
version = buildNumber

repositories {
    mavenCentral()
    maven("https://raw.githubusercontent.com/OpenRune/hosting/master")
    maven("https://jitpack.io")
}


subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")

    group = "dev.or2"
    version = buildNumber

    repositories {
        mavenCentral()
        maven("https://raw.githubusercontent.com/OpenRune/hosting/master")
        maven("https://jitpack.io")
    }

    dependencies {
        implementation("dev.or2:all:2.3.1")
        implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger:1.0.3")
    }

    java.sourceCompatibility = JavaVersion.VERSION_11

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    if (project.name != "git-dumper") {
        publishing {
            publications {
                create<MavenPublication>("mavenJava") {
                    from(components["java"])

                    artifactId = project.name

                    pom {
                        name.set("OpenRune - ${project.name}")
                        description.set("Module ${project.name} of the OpenRune project.")
                        url.set("https://github.com/OpenRune")

                        licenses {
                            license {
                                name.set("Apache-2.0")
                                url.set("https://opensource.org/licenses/Apache-2.0")
                            }
                        }

                        developers {
                            developer {
                                id.set("openrune")
                                name.set("OpenRune Team")
                                email.set("contact@openrune.dev")
                            }
                        }

                        scm {
                            connection.set("scm:git:git://github.com/OpenRune.git")
                            developerConnection.set("scm:git:ssh://github.com/OpenRune.git")
                            url.set("https://github.com/OpenRune")
                        }
                    }
                }
            }

            repositories {
                maven {
                    url = uri(buildDirectory)
                }
            }
        }
    }
}