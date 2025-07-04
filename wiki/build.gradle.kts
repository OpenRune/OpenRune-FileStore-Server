dependencies {
    api(project(":wiki-model"))
    
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.apache.commons:commons-compress:1.26.0")
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("org.sweble.wikitext:swc-engine:3.1.9")
    implementation("com.moandjiezana.toml:toml4j:0.7.2")
    implementation("javax.xml.bind:jaxb-api:2.3.1")

    implementation("me.tongfei:progressbar:0.9.2")
}