package fe.clearurlskt

fun Map<String, String?>.keyValueMapToString(): String {
    return map { (key, value) ->
        if (value != null) "$key=$value"
        else key
    }.joinToString("&")
}

