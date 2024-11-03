package fe.clearurlskt


import fe.relocated.org.apache.hc.core5.core5.net.URIBuilder
import fe.std.uri.ParserFailure
import fe.std.uri.UriParseResult
import fe.std.uri.Url
import java.io.PrintStream
import java.net.URL
import java.net.URLDecoder

object ClearURL {
    private fun isException(provider: Provider, editUrl: String): Boolean {
        for (exception in provider.exceptions) {
            if (exception.containsMatchIn(editUrl)) {
                return true
            }
        }

        return false
    }

    private fun handleRedirections(
        provider: Provider,
        providers: List<Provider>,
        editUrl: String,
        debugWriter: PrintStream? = null,
    ): String? {
        for (redirection in provider.redirections) {
            val result = redirection.find(editUrl)
            debugWriter?.println("Redirection: $redirection $result")
            if (result == null) continue

            val (_, redirect) = result.groupValues
            val resultUrl = URLDecoder.decode(redirect, Charsets.UTF_8)
            val urlObj = URL(editUrl)

            if (!resultUrl.contains("://")) {
                return buildString {
                    append(urlObj.protocol).append("://").append(resultUrl)
                }
            }

            return clearUrl(resultUrl.toString(), providers, debugWriter)
        }

        return null
    }

    private fun applyRawRules(provider: Provider, url: String, debugWriter: PrintStream? = null): String {
        var mutEditUrl = url
        for (rawRule in provider.rawRules) {
            val preReplaceUrl = mutEditUrl
            mutEditUrl = mutEditUrl.replace(rawRule, "")

            if (preReplaceUrl != url) {
                debugWriter?.println("Raw url: $preReplaceUrl $url")
            }
        }

        return mutEditUrl
    }

    private fun applyRules(parseResult: UriParseResult, provider: Provider, debugWriter: PrintStream? = null): String? {
        val parsedUri = parseResult as Url

        val fields = parsedUri.queryParams.toMap(LinkedHashMap())
        val fragments = parsedUri.toFragmentMap()

        var fieldsChanged = 0
        var fragmentsChanged = 0

        if (fields.isEmpty() && fragments.isEmpty()) {
            return null
        }

        for (rule in provider.rules) {
            val removeFields = mutableListOf<String>()
            for ((field, _) in fields) {
                if (!rule.containsMatchIn(field)) continue

                removeFields.add(field)
                fieldsChanged++
                debugWriter?.println("\tRemoving field $field")
            }

            for (field in removeFields) {
                fields.remove(field)
            }

            val removeFragments = mutableListOf<String>()
            for ((fragment, _) in fragments) {
                if (!rule.containsMatchIn(fragment)) continue

                removeFragments.add(fragment)
                fragmentsChanged++
                debugWriter?.println("\tRemoving fragment $fragment")
            }

            for (fragment in removeFragments) {
                fragments.remove(fragment)
            }
        }

        debugWriter?.println("\tField changes: $fieldsChanged, Fragment changes: $fragmentsChanged")
        // If no fields/fragments have been removed, set the encoded query/fragment which will be used over the decoded field/fragment map if set
//        val encodedQuery = if (fieldsChanged == 0) parseResult.encodedQuery else null
//        val encodedFragment = if (fragmentsChanged == 0) parseResult.encodedFragment else null

        val builder = URIBuilder().apply {
            scheme = parsedUri.scheme
//            uri = parsedUri.uri
//            parsedUri.encodedAuthority
//            encodedQuery = parsedUri.encodedQuery

            host = parsedUri.host
            port = parsedUri.port
            userInfo = parsedUri.userInfo
            pathSegments = parsedUri.pathSegments
            fragment = fragments.keyValueMapToString().takeIf { it.isNotEmpty() }

            setParameters(fields)
        }

        return builder.build().toString()
    }

    fun clearUrl(url: String, providers: List<Provider>, debugWriter: PrintStream? = null): String {
        var editUrl = url.trim()
        for (provider in providers) {
            if (!provider.url.containsMatchIn(editUrl)) continue

            debugWriter?.println("${provider.key} has match for $editUrl")

            if (isException(provider, editUrl)) {
                return editUrl
            }

            val redirectionUrl = handleRedirections(provider, providers, editUrl, debugWriter)
            if (redirectionUrl != null) {
                return redirectionUrl
            }

            editUrl = applyRawRules(provider, editUrl, debugWriter)

            val parseResult = Url(editUrl)
            if (parseResult is ParserFailure) {
                debugWriter?.println("Failed to parse $editUrl: ${parseResult.exception.message}")
                return editUrl
            }

            val result = applyRules(parseResult, provider, debugWriter) ?: continue
            editUrl = result
        }

        return editUrl
    }
}
