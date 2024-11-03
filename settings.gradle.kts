import java.util.*
import kotlin.experimental.ExperimentalTypeInference

rootProject.name = "clearurl"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }

    plugins {
        id("de.fayard.refreshVersions") version "0.60.5"
        id("net.nemerosa.versioning") version "3.1.0"
        kotlin("jvm") version "2.0.20"
    }
}

plugins {
    id("de.fayard.refreshVersions")
}

include(":core")

fun substitute(directory: Any, dependency: String, substitutes: Map<String, String>) {
    includeBuild(directory) {
        dependencySubstitution {
            for ((artifact, project) in substitutes) {
                substitute(module("$dependency:$artifact")).using(project(":$project"))
            }
        }
    }
}

@OptIn(ExperimentalTypeInference::class)
fun Any?.trySubstitute(
    dependency: String,
    @BuilderInference builderAction: MutableMap<String, String>.() -> Unit = {},
) {
    this?.let { substitute(this, dependency, buildMap(builderAction)) }
}


fun hasEnv(name: String): Boolean {
    return System.getenv(name)?.toBooleanStrictOrNull() == true
}


val isCI = hasEnv("CI")
val isJitPack = hasEnv("JITPACK")

val localProperties = file("local.properties")
val devProperties: Properties? = if (localProperties.exists()) {
    Properties().apply {
        localProperties.reader().use { load(it) }
    }
} else null

val isDev = (devProperties?.get("dev")?.toString()?.toBooleanStrictOrNull() == true)

if (devProperties != null && isDev && (!isCI && !isJitPack)) {
    devProperties["kotlin-ext.dir"]?.trySubstitute("com.gitlab.grrfe.kotlin-ext") {
        this["core"] = "core"
        this["io"] = "io"
        this["java-time"] = "java-time"
        this["result-core"] = "result:result-core"
        this["result-assert"] = "result:result-assert"
        this["uri"] = "uri"
        this["test"] = "test"
    }
}
