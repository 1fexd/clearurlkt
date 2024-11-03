package fe.clearurlskt

import fe.std.uri.Url

fun Url.toFragmentMap(): MutableMap<String, String?> {
    return fragment
        ?.split("&")
        ?.map { it.split("=") }
        ?.associateTo(LinkedHashMap()) { it[0] to it.getOrNull(1) }
        ?: mutableMapOf()
}

fun Map<String, String?>.keyValueMapToString(): String {
    return map { (key, value) ->
        if (value != null) "$key=$value"
        else key
    }.joinToString("&")
}
