plugins {
    kotlin("jvm") version "1.9.0"
    java
    `maven-publish`
    id("net.nemerosa.versioning") version "3.0.0"
}

group = "fe.clearurlkt"
version = versioning.info.tag ?: versioning.info.full

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.gitlab.grrfe:gson-ext:11.0.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.1fexd:uriparser:0.0.7")

    testImplementation(kotlin("test"))
}

tasks.withType<Jar> {
    sourceSets.main.get().output.resourcesDir?.listFiles()?.map { it.name }?.let { exclude(it) }
}

tasks.test {
    useJUnitPlatform()
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

