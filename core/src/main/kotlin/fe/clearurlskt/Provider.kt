package fe.clearurlskt

import com.google.gson.JsonObject
import fe.gson.context.GlobalGsonContext
import fe.gson.extension.json.`object`.asArray
import fe.gson.extension.json.`object`.asBooleanOrNull
import fe.gson.extension.json.`object`.asString

data class Provider(
    val key: String,
    val url: Regex,
    val completeProvider: Boolean,
    val rules: List<Regex>,
    val rawRules: List<Regex>,
    val referralMarketing: List<Regex>,
    val exceptions: List<Regex>,
    val redirections: List<Regex>,
)

fun String.toIgnoreCaseRegex(exactly: Boolean = false): Regex {
    return Regex(if (exactly) "^$this$" else this, RegexOption.IGNORE_CASE)
}

fun JsonObject.arrayByNameToIgnoreCaseRegexList(name: String, exactly: Boolean = false): List<Regex> {
    return asArray(name).map { it.asJsonPrimitive.asString.toIgnoreCaseRegex(exactly) }
}

fun loadClearUrlsProviders(json: JsonObject): List<Provider> {
    var globalRulesProvider: Provider? = null
    val providers = json.entrySet().mapNotNull { (key, element) ->
        val obj = element as JsonObject

        val provider = Provider(
            key,
            obj.asString("urlPattern").toIgnoreCaseRegex(),
            obj.asBooleanOrNull("completeProvider") ?: false,
            obj.arrayByNameToIgnoreCaseRegexList("rules", true),
            obj.arrayByNameToIgnoreCaseRegexList("rawRules"),
            obj.arrayByNameToIgnoreCaseRegexList("referralMarketing"),
            obj.arrayByNameToIgnoreCaseRegexList("exceptions"),
            obj.arrayByNameToIgnoreCaseRegexList("redirections"),
        )

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
