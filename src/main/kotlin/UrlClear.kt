import java.net.URI
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

fun String.querySplit(fragment: Boolean = false): MutableMap<String, out String?> {
    val mutableMap = mutableMapOf<String, String>()
    if (!this.contains("&") && !this.contains("=") && fragment) {
        return mutableMapOf(this to null)
    }

    this.split("&").mapNotNull {
        with(it.split("=")) {
            if (this.size == 2) {
                this[0] to this[1]
            } else null
        }
    }.forEach { (key, value) ->
        if (!mutableMap.containsKey(key)) {
            mutableMap[key] = value
        }
    }

    return mutableMap
}

fun Map<String, String?>.makeQuery(): String {
    return this.map {
        if (it.value != null) {
            "${it.key}=${it.value}"
        } else {
            it.key
        }
    }.joinToString("&")
}

fun urlClear(url: String, debugPrint: Boolean = true, providers: List<Provider>): String {
    var url = url
    providers.forEach { provider ->
        if (provider.urlPatternRegex.containsMatchIn(url)) {
            println(provider.key)
            var changes = false

            provider.exceptions.forEach {
//                println("\tException $it")
                if (it.containsMatchIn(url)) {
                    return url
                }
            }

            provider.redirections.forEach {
                val result = it.matchEntire(url)
                if (result != null) {
                    val (_, redirect) = result.groupValues
                    val resultUrl = URLDecoder.decode(redirect, StandardCharsets.UTF_8)
                    val urlObj = URL(url)

                    if (!resultUrl.contains("://")) {
                        return buildString {
                            append(urlObj.protocol).append("://").append(resultUrl)
                        }
                    }

                    return urlClear(resultUrl.toString(), debugPrint, providers)
                }
            }

            provider.rawRules.forEach { rawRule ->
                val preReplaceUrl = url
                url = url.replace(rawRule, "")

                if (preReplaceUrl != url) {
                    println("Raw url: $preReplaceUrl $url")
                    changes = true
                }
            }

            val uriObj = URI(url)
            val fields = uriObj.query?.querySplit() ?: mutableMapOf()
            val fragments = uriObj.fragment?.querySplit(fragment = true) ?: mutableMapOf()

            println("\tFields: $fields (${uriObj.query}), Fragments: $fragments (${uriObj.fragment})")

            provider.rules.forEach { rule ->
                val removeFields = mutableListOf<String>()
                fields.forEach { (field, _) ->
                    if (rule.containsMatchIn(field)) {
                        removeFields.add(field)
                        println("\tRemoving field $field")
                        changes = true
                    }
                }

                removeFields.forEach { fields.remove(it) }

                val removeFragments = mutableListOf<String>()
                fragments.forEach { (fragment, _) ->
                    if (rule.containsMatchIn(fragment)) {
                        fragments.remove(fragment)
                        println("\tRemoving fragment $fragment")
                        changes = true
                    }
                }

                removeFragments.forEach { fragments.remove(it) }
            }

            var rebuiltUrl = buildString {
                append(uriObj.scheme).append("://")
                if (uriObj.userInfo != null) {
                    append(uriObj.userInfo).append("@")
                }

                append(uriObj.host)
                if (uriObj.port != -1) {
                    append(":").append(uriObj.port)
                }

                val path = uriObj.path
                append(path)
            }

            println("\tRebuilt url: $rebuiltUrl")

            if (fields.isNotEmpty()) {
                rebuiltUrl += "?" + fields.makeQuery()
            }

            println("\tRebuilt url+fields: $rebuiltUrl")

            if ((uriObj.query != null && fields.isEmpty()) || (uriObj.query != fields.makeQuery())) {
                changes = true
            }

            if (fragments.isNotEmpty()) {
                rebuiltUrl += "#" + fragments.makeQuery()
            }

            if ((uriObj.fragment != null && fragments.isEmpty()) || (uriObj.fragment != fragments.makeQuery())) {
                changes = true
            }

            println("\tRebuilt url+fragments: $rebuiltUrl")
            println("\tProvider has changes: $changes")
            if (changes) {
                return rebuiltUrl
            }
        }
    }

    return url
}
