rootProject.name = "clearurl"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.gitlab.grrfe.common-gradle-plugin") {
                useModule("${requested.id.id}:library:0.0.40")
            }
        }
    }
}

includeBuild("../gson-ext")
includeBuild("../uriparser")
