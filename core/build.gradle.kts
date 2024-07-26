import fe.buildsrc.MetadataGeneratorTask
import fe.buildsrc.UpdateRulesTask

plugins {
    java
    kotlin("jvm")
    id("net.nemerosa.versioning") version "3.1.0"
    `maven-publish`
}

group = "fe.clearurlkt"
version = versioning.info.tag ?: versioning.info.full

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    api("com.gitlab.grrfe.gson-ext:core:16.0.0-gson2-koin3")
    api("com.github.1fexd:uriparser:0.0.11")

    testImplementation(kotlin("test"))
}
val generatedSrcDir = project.file("build/generated")

val main by sourceSets
main.java.srcDir(generatedSrcDir)

val generateMetadata = tasks.register<MetadataGeneratorTask>("generateMetadata") {
    group = "build"
    dir = generatedSrcDir
}

val build by tasks
build.dependsOn(generateMetadata)

val updateRules = tasks.register<UpdateRulesTask>("updateRules") {
    file = "src/main/resources/clearurls.json"
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            version = project.version.toString()

            from(components["java"])
        }
    }
}
