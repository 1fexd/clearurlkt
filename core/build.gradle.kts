import fe.buildsrc.MetadataGeneratorTask
import fe.buildsrc.UpdateRulesTask
import fe.buildsrc.dependency.Grrfe
import fe.buildsrc.publishing.PublicationComponent
import fe.buildsrc.publishing.asProvider
import fe.buildsrc.publishing.publish
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
    id("net.nemerosa.versioning")
    `maven-publish`
}

group = "fe.clearurlkt"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    api(Grrfe.ext.gson)
    api(Grrfe.std.uri)

    testImplementation(kotlin("test"))
    testImplementation("com.willowtreeapps.assertk:assertk:_")
    testImplementation(Grrfe.std.test)
}


val generatedSrcDir: File = layout.buildDirectory.dir("generated/sources/metadata/main/java").get().asFile

val main by sourceSets
main.java.srcDir(generatedSrcDir)

val generateMetadata = tasks.register<MetadataGeneratorTask>("generateMetadata") {
    group = "build"
    dir = generatedSrcDir
}

val assemble by tasks
assemble.dependsOn(generateMetadata)

val updateRules = tasks.register<UpdateRulesTask>("updateRules") {
    file = "src/main/resources/clearurls.json"
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<KotlinCompile> {
    compilerOptions.freeCompilerArgs.add("-Xallow-kotlin-package")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing.publish(
    project,
    group.toString(),
    versioning.asProvider(project),
    PublicationComponent.JAVA
)
