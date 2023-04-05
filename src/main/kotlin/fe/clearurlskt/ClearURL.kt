package fe.clearurlskt

import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
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

fun printlnDebug(str: String, debugPrint: Boolean) {
    if (debugPrint) {
        println(str)
    }
}

fun clearUrl(url: String, providers: List<Provider>, debugPrint: Boolean = false): String {
    var editUrl = url.trim()
    providers.forEach { provider ->
        if (provider.urlPatternRegex.containsMatchIn(editUrl)) {
            printlnDebug(provider.key, debugPrint)
            var changes = false

            provider.exceptions.forEach {
                if (it.containsMatchIn(editUrl)) {
                    return editUrl
                }
            }

            provider.redirections.forEach {
                val result = it.find(editUrl)
                printlnDebug("Redirection: $it $result", debugPrint)
                if (result != null) {
                    val (_, redirect) = result.groupValues
                    val resultUrl = URLDecoder.decode(redirect, StandardCharsets.UTF_8)
                    val urlObj = URL(editUrl)

                    if (!resultUrl.contains("://")) {
                        return buildString {
                            append(urlObj.protocol).append("://").append(resultUrl)
                        }
                    }

                    return clearUrl(resultUrl.toString(), providers, debugPrint)
                }
            }

            provider.rawRules.forEach { rawRule ->
                val preReplaceUrl = editUrl
                editUrl = editUrl.replace(rawRule, "")

                if (preReplaceUrl != editUrl) {
                    printlnDebug("Raw url: $preReplaceUrl $editUrl", debugPrint)
                    changes = true
                }
            }

            val uriObj = try {
                URI(editUrl)
            } catch (e: URISyntaxException) {
                printlnDebug(e.message!!, debugPrint)
                if(e.index > -1 && e.input[e.index] == '#' && e.reason == "Illegal character in fragment"){
                    // duplicate hash sign detected
                    val fragmentHashIndex = editUrl.indexOf("#")
                    val newUrl = editUrl.substring(
                        0,
                        fragmentHashIndex
                    ) + "#" + URLEncoder.encode(editUrl.substring(fragmentHashIndex + 1), StandardCharsets.UTF_8)

                    printlnDebug("\tNew url: $newUrl", debugPrint)
                    URI(newUrl)
                } else {
                    return url
                }
            }

            val fields = uriObj.query?.querySplit() ?: mutableMapOf()
            val fragments = uriObj.fragment?.querySplit(fragment = true) ?: mutableMapOf()

            printlnDebug("\tFields: $fields (${uriObj.query}), Fragments: $fragments (${uriObj.fragment})", debugPrint)

            provider.rules.forEach { rule ->
                val removeFields = mutableListOf<String>()
                fields.forEach { (field, _) ->
                    if (rule.containsMatchIn(field)) {
                        removeFields.add(field)
                        printlnDebug("\tRemoving field $field", debugPrint)
                        changes = true
                    }
                }

                removeFields.forEach { fields.remove(it) }

                val removeFragments = mutableListOf<String>()
                fragments.forEach { (fragment, _) ->
                    if (rule.containsMatchIn(fragment)) {
                        fragments.remove(fragment)
                        printlnDebug("\tRemoving fragment $fragment", debugPrint)
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

            printlnDebug("\tRebuilt url: $rebuiltUrl", debugPrint)

            val fieldsQuery = fields.makeQuery()
            if (fields.isNotEmpty()) {
                rebuiltUrl += "?$fieldsQuery"
            }

            printlnDebug("\tRebuilt url+fields: $rebuiltUrl", debugPrint)

            if (uriObj.query != null && (fields.isEmpty() || uriObj.query != fieldsQuery)) {
                printlnDebug("\tUriObj.query: ${uriObj.query} $fields $fieldsQuery", debugPrint)
                changes = true
            }

            val fragmentsQuery = fragments.makeQuery()
            if (fragments.isNotEmpty()) {
                rebuiltUrl += "#$fragmentsQuery"
            }

            if (uriObj.fragment != null && (fragments.isEmpty() || uriObj.fragment != fragmentsQuery)) {
                printlnDebug("\tUriObj.fragment: ${uriObj.fragment}, $fragments, ${fragmentsQuery};", debugPrint)
                changes = true
            }

            printlnDebug("\tRebuilt url+fragments: $rebuiltUrl", debugPrint)
            printlnDebug("\tfe.clearurlskt.Provider has changes: $changes", debugPrint)
            if (changes) {
                return rebuiltUrl
            }
        }
    }

    return editUrl
}
