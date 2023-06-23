package fe.clearurlskt

import fe.uribuilder.*
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

fun printlnDebug(str: String, debugPrint: Boolean) {
    if (debugPrint) println(str)
}

fun clearUrl(url: String, providers: List<Provider>, debugPrint: Boolean = false): String {
    var editUrl = url.trim()
    providers.forEach { provider ->
        if (provider.urlPatternRegex.containsMatchIn(editUrl)) {
            printlnDebug(provider.key, debugPrint)

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
                }
            }

            val parsedUri = UriParser.parseUri(editUrl)

            val fields = parsedUri.queryParams.associateTo(LinkedHashMap<String, String>()) { it.name to it.value }
            val fragments = parsedUri.fragments
            val domain = parsedUri.uri.urlWithoutParamsAndHash().toString()

            printlnDebug("\tFields: $fields, Fragments: $fragments (${parsedUri.fragment})", debugPrint)
            if (fields.isNotEmpty() || fragments.isNotEmpty()) {
                provider.rules.forEach { rule ->
                    val removeFields = mutableListOf<String>()
                    fields.forEach { (field, _) ->
                        if (rule.containsMatchIn(field)) {
                            removeFields.add(field)
                            printlnDebug("\tRemoving field $field", debugPrint)
                        }
                    }

                    removeFields.forEach { fields.remove(it) }

                    val removeFragments = mutableListOf<String>()
                    fragments.forEach { (fragment, _) ->
                        if (rule.containsMatchIn(fragment)) {
                            fragments.remove(fragment)
                            printlnDebug("\tRemoving fragment $fragment", debugPrint)
                        }
                    }

                    removeFragments.forEach { fragments.remove(it) }
                }

                var finalURL = domain

                if (fields.isNotEmpty()) {
                    finalURL += "?" + fields.keyValueMapToString()
                }

                if (fragments.isNotEmpty()) {
                    finalURL += "#" + fragments.keyValueMapToString()
                }

                editUrl = finalURL
            }
        }
    }

    return editUrl
}
