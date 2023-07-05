package fe.clearurlskt

import fe.uribuilder.*
import java.io.PrintStream
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

fun clearUrl(url: String, providers: List<Provider>, debugWriter: PrintStream? = null): String {
    var editUrl = url.trim()
    providers.forEach { provider ->
        if (provider.url.containsMatchIn(editUrl)) {
            debugWriter?.println(provider.key)

            provider.exceptions.forEach {
                if (it.containsMatchIn(editUrl)) {
                    return editUrl
                }
            }

            provider.redirections.forEach {
                val result = it.find(editUrl)
                debugWriter?.println("Redirection: $it $result")
                if (result != null) {
                    val (_, redirect) = result.groupValues
                    val resultUrl = URLDecoder.decode(redirect, StandardCharsets.UTF_8)
                    val urlObj = URL(editUrl)

                    if (!resultUrl.contains("://")) {
                        return buildString {
                            append(urlObj.protocol).append("://").append(resultUrl)
                        }
                    }

                    return clearUrl(resultUrl.toString(), providers, debugWriter)
                }
            }

            provider.rawRules.forEach { rawRule ->
                val preReplaceUrl = editUrl
                editUrl = editUrl.replace(rawRule, "")

                if (preReplaceUrl != editUrl) {
                    debugWriter?.println("Raw url: $preReplaceUrl $editUrl")
                }
            }

            val parsedUri = UriParser.parseUri(editUrl)

            val fields = parsedUri.queryParams.associateTo(LinkedHashMap<String, String>()) { it.name to it.value }
            val fragments = parsedUri.fragments
            val domain = parsedUri.uri.urlWithoutParamsAndHash().toString()

            debugWriter?.println("\tFields: $fields, Fragments: $fragments (${parsedUri.fragment})")
            if (fields.isNotEmpty() || fragments.isNotEmpty()) {
                provider.rules.forEach { rule ->
                    val removeFields = mutableListOf<String>()
                    fields.forEach { (field, _) ->
                        if (rule.containsMatchIn(field)) {
                            removeFields.add(field)
                            debugWriter?.println("\tRemoving field $field")
                        }
                    }

                    removeFields.forEach { fields.remove(it) }

                    val removeFragments = mutableListOf<String>()
                    fragments.forEach { (fragment, _) ->
                        if (rule.containsMatchIn(fragment)) {
                            fragments.remove(fragment)
                            debugWriter?.println("\tRemoving fragment $fragment")
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
