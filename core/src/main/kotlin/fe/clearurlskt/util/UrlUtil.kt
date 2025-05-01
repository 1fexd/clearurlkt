package fe.clearurlskt.util

import fe.std.uri.Url

public fun Url.toFragmentMap(): MutableMap<String, String?> {
    return fragment
        ?.split("&")
        ?.map { it.split("=") }
        ?.associateTo(LinkedHashMap()) { it[0] to it.getOrNull(1) }
        ?: mutableMapOf()
}

public fun Map<String, String?>.keyValueMapToString(): String {
    return map { (key, value) ->
        if (value != null) "$key=$value"
        else key
    }.joinToString("&")
}
