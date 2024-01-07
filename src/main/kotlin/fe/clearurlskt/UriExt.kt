package fe.clearurlskt

import java.net.URI

fun URI.urlWithoutParamsAndHash(): URI {
    var newUrl = toString()
    if (rawQuery?.isNotEmpty() == true) {
        newUrl = newUrl.replace("?$rawQuery", "")
    }

    if (rawFragment?.isNotEmpty() == true) {
        newUrl = newUrl.replace("#$rawFragment", "")
    }

    return URI(newUrl)
}
