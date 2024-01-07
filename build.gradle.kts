import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import fe.buildsrc.Package.relocatePackages

plugins {
    kotlin("jvm") version "1.9.22"
    `java-library`
    `maven-publish`
    id("net.nemerosa.versioning") version "3.0.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "fe.clearurlkt"
version = versioning.info.tag ?: versioning.info.full

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

val shadowImplementation = configurations.create("shadowImplementation"){
    configurations.implementation.get().extendsFrom(this)
    isTransitive = false
}

dependencies {
    api(kotlin("stdlib"))

    shadowImplementation("com.google.code.gson:gson:2.+")
    shadowImplementation("com.gitlab.grrfe.gson-ext:core:14.0.2-gson2-koin3")
    shadowImplementation("com.github.1fexd:uriparser:0.0.10")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

val shadowJarTask = tasks.named<ShadowJar>("shadowJar") {
    mergeServiceFiles()
    exclude("META-INF/**/*")

    project.relocatePackages(shadowImplementation).forEach { (from, to) ->
        relocate(from, to)
    }

    archiveClassifier.set("")
    minimize()
    configurations = listOf(shadowImplementation)
}


tasks.named("jar").configure {
    enabled = false
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("shadow") {
            setArtifacts(listOf(shadowJarTask.get()))
            groupId = project.group.toString()
            version = project.version.toString()
        }
    }
}

//configurations {
//    artifacts {
//        runtimeElements(shadowJarTask)
//        apiElements(shadowJarTask)
//    }
//}

tasks.whenTaskAdded {
    if (name == "generateMetadataFileForPluginShadowPublication") {
        dependsOn(shadowJarTask)
    }
}
