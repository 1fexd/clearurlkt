plugins {
    `java-platform`
    `maven-publish`
    id("net.nemerosa.versioning") version "3.1.0"
}

group = "fe.clearurlkt"
version = versioning.info.tag ?: versioning.info.full

dependencies {
    constraints {
        api("com.gitlab.grrfe.gson-ext:core:16.0.0-gson2-koin3")
        api("com.gitlab.grrfe.gson-ext:koin:16.0.0-gson2-koin3")
        api("com.github.1fexd:uriparser:0.0.11")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "bom"
            version = project.version.toString()

            from(components["javaPlatform"])
        }
    }
}

