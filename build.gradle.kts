import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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

val implementation: Configuration by configurations
val shadowImplementation: Configuration by configurations.creating
implementation.extendsFrom(shadowImplementation)

dependencies {
    api(kotlin("stdlib"))

    implementation("com.google.code.gson:gson:2.10.1")

    shadowImplementation("com.gitlab.grrfe:gson-ext:11.0.0") {
        isTransitive = false
    }
    shadowImplementation("com.github.1fexd:uriparser:0.0.9") {
        isTransitive = false
    }

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

val shadowJarTask = tasks.named<ShadowJar>("shadowJar") {
    mergeServiceFiles()
    exclude("META-INF/**/*")

    listOf("fe.gson", "fe.uribuilder", "org.apache.hc.core5").forEach { pkg ->
        relocate(pkg, "fe.clearurlskt.internal.$pkg")
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
        println(name)
        dependsOn(shadowJarTask)
    }
}
