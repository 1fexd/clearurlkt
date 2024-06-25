plugins {
    kotlin("jvm")
    id("net.nemerosa.versioning") version "3.1.0"
    `maven-publish`
}

group = "fe.clearurlkt"
version = versioning.info.tag ?: versioning.info.full

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    api(platform("com.github.1fexd:super:0.0.2"))
    api("com.gitlab.grrfe.gson-ext:core")
    api("com.github.1fexd:uriparser")

    testImplementation(kotlin("test"))
//    implementation("org.apache.httpcomponents.core5:httpcore5:5.3-alpha1")
//    implementation(project(":uriparser"))
//    relocate("com.google.code.gson:gson:2.10.1")
//    relocate("com.gitlab.grrfe:gson-ext:11.0.0")
//    relocate("com.gitlab.grrfe.gson-ext:core:14.0.2-gson2-koin3")
//    relocate("com.github.1fexd:uriparser:0.0.11")
}

kotlin {
    jvmToolchain(17)
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
