import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd
import fe.buildsrc.MetadataGeneratorTask
import fe.buildsrc.UpdateRulesTask

plugins {
}

dependencies {
    api("com.google.code.gson:gson:2.11.0")
    api(platform(Grrfe.gsonExt.bom))
    api(Grrfe.gsonExt.core)

    api(platform(Grrfe.std.bom))
    api(Grrfe.std.result.core)
    api(Grrfe.std.uri)
    api(_1fexd.signify)

    testImplementation(kotlin("test"))
    testImplementation("com.willowtreeapps.assertk:assertk:_")
    testImplementation(Grrfe.std.test)
}


val generatedSrcDir: File = layout.buildDirectory.dir("generated/sources/metadata/main/java").get().asFile

val main by sourceSets
main.java.srcDir(generatedSrcDir)

val generateMetadata = tasks.register<MetadataGeneratorTask>("generateMetadata") {
    group = "build"
    dir = generatedSrcDir
}

val assemble by tasks
assemble.dependsOn(generateMetadata)

val updateRules = tasks.register<UpdateRulesTask>("updateRules") {
    file = "src/main/resources/fe/clearurlskt/clearurls.json"
}
