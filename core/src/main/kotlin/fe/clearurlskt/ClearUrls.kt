package fe.clearurlskt


import fe.clearurlskt.Modification.*
import fe.clearurlskt.provider.Provider
import fe.clearurlskt.util.keyValueMapToString
import fe.clearurlskt.util.toFragmentMap
import fe.relocated.org.apache.hc.core5.core5.net.PercentCodec
import fe.std.result.isFailure
import fe.std.uri.Url
import fe.std.uri.UrlFactory
import fe.std.uri.extension.buildUrl

class ClearUrls(
    private val providers: List<Provider>,
) {
    private suspend fun SequenceScope<ClearUrlOperation>.isException(provider: Provider, editUrl: String): Boolean {
        for (regex in provider.exceptions) {
            if (!regex.containsMatchIn(editUrl)) continue

            yield(Exception(provider.key, editUrl, regex))
            return true
        }

        return false
    }

    private suspend fun SequenceScope<ClearUrlOperation>.handleRedirections(provider: Provider, url: String): String? {
        for (regex in provider.redirections) {
            val result = regex.find(url)
            if (result == null) continue

            val (_, redirect) = result.groupValues

            val resultUrl = PercentCodec.decode(redirect, Charsets.UTF_8)
            if (!resultUrl.contains("://")) {
                return UrlFactory.fixHttpUrl(resultUrl)
            }

            yield(Redirection(provider.key, url, resultUrl, regex))
            return clearUrl(resultUrl.toString())
        }

        return null
    }

    private suspend fun SequenceScope<ClearUrlOperation>.applyRawRules(provider: Provider, url: String): String {
        var mutUrl = url
        for (regex in provider.rawRules) {
            val preReplaceUrl = mutUrl
            mutUrl = mutUrl.replace(regex, "")

            if (preReplaceUrl == mutUrl) continue
            yield(RawRule(provider.key, preReplaceUrl, mutUrl, regex))
        }

        return mutUrl
    }

    private fun <K, V> MutableMap<K, V>.removeIfKey(predicate: (K) -> Boolean): MutableSet<K> {
        val removeKeys = mutableSetOf<K>()
        for (key in keys) {
            if (!predicate(key)) continue
            removeKeys.add(key)
        }

        for (key in removeKeys) {
            remove(key)
        }

        return removeKeys
    }

    private suspend fun SequenceScope<ClearUrlOperation>.applyRules(provider: Provider, url: Url): String? {
        val fields = url.queryParams.toMap(LinkedHashMap())
        val fragments = url.toFragmentMap()

        val fieldChanges = mutableSetOf<String>()
        val fragmentChanges = mutableSetOf<String>()

        if (fields.isEmpty() && fragments.isEmpty()) return null

        for (rule in provider.rules) {
            fieldChanges.addAll(fields.removeIfKey { rule.containsMatchIn(it) })
            fragmentChanges.addAll(fragments.removeIfKey { rule.containsMatchIn(it) })
        }

        val inputUrl = url.toString()
        val url = buildUrl {
            scheme = url.scheme
            host = url.host
            port = url.port
            userInfo = url.userInfo
            pathSegments = url.pathSegments
            fragment = fragments.keyValueMapToString().takeIf { it.isNotEmpty() }
            setParameters(fields)
        }

        val resultUrl = url.toString()
        yield(ParameterRemoval(provider.key, inputUrl, resultUrl, fieldChanges, fragmentChanges))

        return resultUrl
    }

    private suspend fun SequenceScope<ClearUrlOperation>.clearUrl(url: String): String {
        var mutUrl = url.trim()
        for (provider in providers) {
            if (!provider.url.containsMatchIn(mutUrl)) continue

            if (isException(provider, mutUrl)) {
                return mutUrl
            }

            val redirectionUrl = handleRedirections(provider, mutUrl)
            if (redirectionUrl != null) {
                return redirectionUrl
            }

            mutUrl = applyRawRules(provider, mutUrl)

            val parseResult = UrlFactory.parse(mutUrl, setPlusAsBlanks = true)
            if (parseResult.isFailure()) {
                yield(ParseFailure(provider.key, mutUrl, parseResult))
                return mutUrl
            }

            mutUrl = applyRules(provider, parseResult.value) ?: continue
        }

        return mutUrl
    }

    fun clearUrl(url: String): Pair<String, List<ClearUrlOperation>> {
        var result = url
        val operations = sequence<ClearUrlOperation> { result = clearUrl(url) }.toList()
        return result to operations
    }
}


