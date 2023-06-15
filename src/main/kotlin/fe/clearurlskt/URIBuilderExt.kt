package fe.clearurlskt

import org.apache.hc.core5.net.URIBuilder
import java.net.URI

fun URIBuilder.fragments(): MutableMap<String, String?> {
    return fragment?.split("&")?.asSequence()?.map { it.split("=") }
        ?.associateTo(LinkedHashMap()) { if (it.size == 2) it[0] to it[1] else it[0] to null } ?: mutableMapOf()
}

fun Map<String, String?>.keyValueMapToString(): String {
    return map { (key, value) ->
        if (value != null) "$key=$value"
        else key
    }.joinToString("&")
}

fun urlWithoutParamsAndHash(uri: URI): URI {
    var newUrl = uri.toString()
    if (uri.rawQuery?.isNotEmpty() == true) {
        newUrl = newUrl.replace("?" + uri.rawQuery, "")
    }

    if (uri.rawFragment?.isNotEmpty() == true) {
        newUrl = newUrl.replace("#" + uri.rawFragment, "")
    }

    return URI(newUrl)
}
