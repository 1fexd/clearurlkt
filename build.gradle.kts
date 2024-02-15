import fe.plugin.library.LibraryConfig.Companion.library

plugins {
    id("com.gitlab.grrfe.common-gradle-plugin")
}

library("fe.clearurlkt") {
    jvm.set(17)
}

dependencies {
    relocate("com.google.code.gson:gson:2.10.1")
    relocate("com.gitlab.grrfe:gson-ext:11.0.0")
//    relocate("com.gitlab.grrfe.gson-ext:core:14.0.2-gson2-koin3")
    relocate("com.github.1fexd:uriparser:0.0.11")
}
