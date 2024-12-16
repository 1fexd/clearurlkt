package fe.clearurlskt.provider

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import fe.gson.extension.io.parseJsonOrNull
import fe.gson.extension.json.array.elementsOrNull
import fe.gson.extension.json.`object`.asArray
import fe.gson.extension.json.`object`.asBooleanOrNull
import fe.gson.extension.json.`object`.asObjectOrNull
import fe.gson.extension.json.`object`.asString
import java.io.InputStream

object ProviderSerializer {
    private fun String.toIgnoreCaseRegex(exactly: Boolean = false): Regex {
        val str = if (exactly) "^$this$" else this
        return Regex(str, RegexOption.IGNORE_CASE)
    }

    private fun JsonObject.arrayByNameToIgnoreCaseRegexList(name: String, exactly: Boolean = false): List<Regex> {
        return asArray(name)
            .elementsOrNull<JsonPrimitive>()
            .mapNotNull { it?.asString?.toIgnoreCaseRegex(exactly) }
    }

    private fun handleProviders(providers: JsonObject): List<Provider> {
        return providers
            .entrySet()
            .mapIndexedNotNull { idx, (key, element) ->
                val obj = element as JsonObject

                Provider(
                    // make sure the globalRules provider is the last one in the list and used as a fallback
                    sortPosition = if (key == "globalRules") Int.MAX_VALUE else idx,
                    key,
                    obj.asString("urlPattern").toIgnoreCaseRegex(),
                    obj.asBooleanOrNull("completeProvider") == true,
                    obj.arrayByNameToIgnoreCaseRegexList("rules", true),
                    obj.arrayByNameToIgnoreCaseRegexList("rawRules"),
                    obj.arrayByNameToIgnoreCaseRegexList("referralMarketing"),
                    obj.arrayByNameToIgnoreCaseRegexList("exceptions"),
                    obj.arrayByNameToIgnoreCaseRegexList("redirections"),
                )
            }
            .sortedBy { it.sortPosition }
    }

    fun handle(stream: InputStream): List<Provider>? {
        return stream
            .parseJsonOrNull<JsonObject>()
            ?.asObjectOrNull("providers")
            ?.let { handleProviders(it) }
    }
}