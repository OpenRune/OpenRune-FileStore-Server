dependencies {
    api(project(":wiki"))

    implementation("org.eclipse.jgit:org.eclipse.jgit:6.9.0.202403050737-r")
}

val generatedDir = layout.buildDirectory.dir("generated/src/main/kotlin")

sourceSets["main"].kotlin.srcDir(generatedDir)

val generateBuildConfig by tasks.registering {
    val outputFile = generatedDir.map { it.file("dev/openrune/wiki/BuildConfig.kt") }

    outputs.file(outputFile)

    doLast {
        val file = outputFile.get().asFile
        file.parentFile.mkdirs()

        val revision = project.findProperty("last_updated_revision")?.toString() ?: "0"
        val last_updated_time = project.findProperty("last_updated_timestamp")?.toString() ?: "0"

        file.writeText(
            """
            package dev.openrune.wiki

            object BuildConfig {
                const val LAST_UPDATED_REVISION = $revision
                const val LAST_UPDATED_TIME : Long = $last_updated_time
            }
            """.trimIndent()
        )
    }
}

tasks.named("compileKotlin") {
    dependsOn(generateBuildConfig)
}