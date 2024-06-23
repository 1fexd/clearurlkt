package fe.clearurlskt

import fe.uribuilder.*
import org.apache.hc.core5.http.message.BasicNameValuePair
import java.io.PrintStream
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object ClearURL {
    fun clearUrl(url: String, providers: List<Provider>, debugWriter: PrintStream? = null): String {
        var editUrl = url.trim()
        providers.forEach { provider ->
            if (provider.url.containsMatchIn(editUrl)) {
                debugWriter?.println("${provider.key} has match for $editUrl")

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

                val parseResult = UriParser.parseUri(editUrl, Charsets.UTF_8)
                if (parseResult is UriParseResult.ParserFailure) {
                    debugWriter?.println("Failed to parse $editUrl: ${parseResult.exception.message}")
                    return editUrl
                }

                val parsedUri = parseResult as UriParseResult.ParsedUri

                val fields = parsedUri.queryParams.associateTo(LinkedHashMap<String, String>()) { it.name to it.value }
                val fragments = parsedUri.fragments

                var fieldsChanged = 0
                var fragmentsChanged = 0
                debugWriter?.println("\tFields: $fields, Fragments: $fragments (${parsedUri.fragment})")
                if (fields.isNotEmpty() || fragments.isNotEmpty()) {
                    provider.rules.forEach { rule ->
                        val removeFields = mutableListOf<String>()
                        fields.forEach { (field, _) ->
                            if (rule.containsMatchIn(field)) {
                                removeFields.add(field)
                                fieldsChanged++
                                debugWriter?.println("\tRemoving field $field")
                            }
                        }

                        removeFields.forEach { fields.remove(it) }

                        val removeFragments = mutableListOf<String>()
                        fragments.forEach { (fragment, _) ->
                            if (rule.containsMatchIn(fragment)) {
                                fragments.remove(fragment)
                                fragmentsChanged++
                                debugWriter?.println("\tRemoving fragment $fragment")
                            }
                        }

                        removeFragments.forEach { fragments.remove(it) }
                    }

                    debugWriter?.println("\tField changes: $fieldsChanged, Fragment changes: $fragmentsChanged")
                    // If no fields/fragments have been removed, set the encoded query/fragment which will be used over the decoded field/fragment map if set
                    val encodedQuery = if (fieldsChanged == 0) parseResult.encodedQuery else null
                    val encodedFragment = if (fragmentsChanged == 0) parseResult.encodedFragment else null

                    val newUri = UriParseResult.ParsedUri(
                        parsedUri.scheme,
                        null,
                        parsedUri.encodedAuthority,
                        parsedUri.uri,
                        parsedUri.host,
                        parsedUri.port,
                        parsedUri.encodedUserInfo,
                        parsedUri.userInfo,
                        parsedUri.encodedPath,
                        parsedUri.pathSegments,
                        parsedUri.pathRootless,
                        encodedQuery,
                        fields.map { BasicNameValuePair(it.key, it.value) },
                        encodedFragment,
                        fragments.keyValueMapToString().takeIf { it.isNotEmpty() },
                        Charsets.UTF_8
                    )

                    editUrl = newUri.build().toString()
                }
            }
        }

        return editUrl
    }

}
