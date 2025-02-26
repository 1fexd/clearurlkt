import fe.buildsrc.MetadataGeneratorTask
import fe.buildsrc.UpdateRulesTask
import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd
import fe.buildlogic.extension.CompilerOption
import fe.buildlogic.extension.addCompilerOptions
import fe.buildlogic.publishing.PublicationComponent
import fe.buildlogic.publishing.publish

plugins {
    kotlin("jvm")
    `maven-publish`
    id("net.nemerosa.versioning")
    id("com.gitlab.grrfe.build-logic-plugin")
}

group = "fe.clearurlkt"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    api(Grrfe.gsonExt.core)
    api(Grrfe.std.result.core)
    api(Grrfe.std.uri)
    api(_1fexd.signify)

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
    file = "src/main/resources/fe/clearurlskt/clearurls.json"
}

kotlin {
    jvmToolchain(21)
    addCompilerOptions(CompilerOption.AllowKotlinPackage)
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing.publish(
    project = project,
    component = PublicationComponent.Java
)
