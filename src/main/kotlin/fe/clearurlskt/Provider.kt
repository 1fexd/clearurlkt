package fe.clearurlskt

import com.google.gson.JsonObject
import fe.gson.extensions.array
import fe.gson.extensions.bool
import fe.gson.extensions.string

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
    var globalRulesProvider: Provider? = null
    val providers = json.entrySet().mapNotNull { (key, element) ->
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

        if (key == "globalRules") {
            globalRulesProvider = provider
            null
        } else provider
    }.toMutableList()

    // make sure the globalRules provider is the last one in the list and used as a fallback
    if (globalRulesProvider != null) {
        providers.add(globalRulesProvider!!)
    }

    return providers
}
