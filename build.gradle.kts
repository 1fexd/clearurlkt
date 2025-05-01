import com.gitlab.grrfe.gradlebuild.common.extension.isPlatform
import com.gitlab.grrfe.gradlebuild.common.extension.isTesting
import com.gitlab.grrfe.gradlebuild.library.publishing.PublicationComponent2
import com.gitlab.grrfe.gradlebuild.library.publishing.PublicationName2
import fe.buildlogic.Plugins
import fe.buildlogic.Version
import fe.buildlogic.applyPlugin
import fe.buildlogic.common.CompilerOption
import fe.buildlogic.common.extension.addCompilerOptions

plugins {
    kotlin("jvm")
    id("net.nemerosa.versioning") apply false
    id("com.gitlab.grrfe.new-build-logic-plugin")
    id("com.gitlab.grrfe.library-build-plugin")
}

val baseGroup = "com.github.1fexd.clearurlkt"

subprojects {
    logger.quiet("Init for $this, isTesting=$isTesting, isPlatform=$isPlatform")

    if (!isPlatform) {
        applyPlugin(Plugins.KotlinJvm)
    }

    applyPlugin(
        Plugins.MavenPublish,
        Plugins.GrrfeNewBuildLogic,
        Plugins.GrrfeLibraryBuild,
        Plugins.NemerosaVersioning
    )

    group = baseGroup
    library {
        if (!isTesting) {
            publication {
                name.set(PublicationName2.Maven)
                component.set(if (isPlatform) PublicationComponent2.JavaPlatform else PublicationComponent2.Java)
            }
        }
    }

    if (!isPlatform) {
        kotlin {
            jvmToolchain(Version.JVM)
            if (!isTesting) {
                explicitApi()
            }
            addCompilerOptions(CompilerOption.WhenGuards)
        }

        java {
            withJavadocJar()
            withSourcesJar()
        }
    }
}
