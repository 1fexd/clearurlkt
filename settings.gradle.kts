rootProject.name = "clearurl"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }

    plugins {
        kotlin("jvm") version "1.9.24"
        id("de.fayard.refreshVersions") version "0.60.5"
        id("org.gradle.maven-publish")
        id("net.nemerosa.versioning") version "3.1.0"
    }
}

include("core")
