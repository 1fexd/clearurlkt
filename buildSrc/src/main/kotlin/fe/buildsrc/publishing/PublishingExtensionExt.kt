package fe.buildsrc.publishing

import net.nemerosa.versioning.VersionInfo
import net.nemerosa.versioning.VersioningExtension
import org.gradle.api.Project
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.provider.Provider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.*

fun PublishingExtension.publish(
    project: Project,
    group: String,
    version: Provider<String>,
    component: String,
) {
    publications {
        register<MavenPublication>("maven") {
            groupId = group
            this.version = version.get()

            project.afterEvaluate {
                from(components[component])
            }
        }
    }
}

typealias VersionStrategy = (VersionInfo) -> String

val DefaultVersionStrategy: VersionStrategy = { info ->
    runCatching { info.tag ?: info.full }.getOrDefault("0.0.0")
}

fun VersioningExtension.asProvider(
    project: Project,
    strategy: VersionStrategy = DefaultVersionStrategy,
): Provider<String> {
    return project.provider {
        val info = computeInfo()
        strategy(info)
    }
}
