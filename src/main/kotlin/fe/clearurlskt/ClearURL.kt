package fe.clearurlskt

import org.apache.hc.core5.net.URIBuilder
import java.net.*
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

            val firstHashIndex = editUrl.indexOf("#")
            if (firstHashIndex > -1 && editUrl.indexOf("#", firstHashIndex + 1) > -1) {
                editUrl = editUrl.substring(0, firstHashIndex) + "#" + URLEncoder.encode(
                    editUrl.substring(firstHashIndex + 1),
                    StandardCharsets.UTF_8
                )
            }

            val uriObj = URI(editUrl)
            val uriBuilder = URIBuilder(uriObj)

            val fields = uriBuilder.queryParams.associateTo(LinkedHashMap<String, String>()) { it.name to it.value }
            val fragments = uriBuilder.fragments()
            val domain = urlWithoutParamsAndHash(uriObj).toString()

            printlnDebug("\tFields: $fields, Fragments: $fragments (${uriBuilder.fragment})", debugPrint)
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
