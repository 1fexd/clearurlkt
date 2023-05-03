import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
    java
    maven
    id("net.nemerosa.versioning") version "3.0.0"
}

group = "fe.clearurlkt"
version = versioning.info.tag ?: versioning.info.full

repositories {
    mavenCentral()
    maven(url="https://jitpack.io")
}

dependencies {
    implementation("com.gitlab.grrfe:GSONKtExtensions:2.1.2")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(kotlin("test"))
}

tasks.withType<Jar> {
    exclude("fetch_latest_clearurls.sh")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
