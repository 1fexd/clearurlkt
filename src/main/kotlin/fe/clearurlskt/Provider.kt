package fe.clearurlskt

import com.google.gson.JsonObject
import fe.gson.extensions.array
import fe.gson.extensions.bool
import fe.gson.extensions.string
import java.io.InputStream

class Provider(val key: String, val urlPattern: String, val completeProvider: Boolean = true) {
    val rules = mutableListOf<Regex>()
    val rawRules = mutableListOf<Regex>()
    val referralMarketing = mutableListOf<Regex>()
    val exceptions = mutableListOf<Regex>()
    val redirections = mutableListOf<Regex>()
    val forceRedirection = false

    val urlPatternRegex by lazy { Regex(urlPattern) }
}

fun loadClearUrlsProviders(json: JsonObject): List<Provider> {
    return json.entrySet().map { (key, element) ->
        val obj = element as JsonObject
        val provider = Provider(key, obj.string("urlPattern") ?: "", obj.bool("completeProvider") ?: false)

//        provider.urlPattern = Regex(obj.string("urlPattern") ?: "", RegexOption.IGNORE_CASE)
        obj.array("rules").forEach {
            provider.rules.add(Regex("^${it.asJsonPrimitive.asString}$", RegexOption.IGNORE_CASE))
        }

        obj.array("rawRules").forEach {
            provider.rawRules.add(Regex(it.asJsonPrimitive.asString, RegexOption.IGNORE_CASE))
        }

        obj.array("referralMarketing").forEach {
            provider.referralMarketing.add(Regex(it.asJsonPrimitive.asString))
        }

        obj.array("exceptions").forEach {
            provider.exceptions.add(Regex(it.asJsonPrimitive.asString))
        }

        obj.array("redirections").forEach {
            provider.redirections.add(Regex(it.asJsonPrimitive.asString))
        }

        return@map provider
    }
}
