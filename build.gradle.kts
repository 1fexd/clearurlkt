import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
}

group = "fe.clearurlkt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url="https://jitpack.io")
}

dependencies {
    implementation("com.gitlab.grrfe:GSONKtExtensions:2.1.2")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
