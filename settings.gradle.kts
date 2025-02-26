import fe.build.dependencies.Grrfe

rootProject.name = "clearurl"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }

    plugins {
        id("de.fayard.refreshVersions") version "0.60.5"
        id("net.nemerosa.versioning") version "3.1.0"
        kotlin("jvm") version "2.1.0" apply false
    }

    when (val gradleBuildDir = extra.properties["gradle.build.dir"]) {
        null -> {
            val gradleBuildVersion = extra.properties["gradle.build.version"]
            val plugins = mapOf(
                "com.gitlab.grrfe.build-settings-plugin" to "com.gitlab.grrfe.gradle-build:build-settings",
                "com.gitlab.grrfe.build-logic-plugin" to "com.gitlab.grrfe.gradle-build:build-logic"
            )

            resolutionStrategy {
                eachPlugin {
                    plugins[requested.id.id]?.let { useModule("$it:$gradleBuildVersion") }
                }
            }
        }

        else -> includeBuild(gradleBuildDir.toString())
    }
}

plugins {
    id("de.fayard.refreshVersions")
    id("com.gitlab.grrfe.build-settings-plugin")
}

extra.properties["gradle.build.dir"]
    ?.let { includeBuild(it.toString()) }

include(":core")

buildSettings {
    substitutes {
        trySubstitute(Grrfe.std, properties["kotlin-ext.dir"])
    }
}
