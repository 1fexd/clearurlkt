package fe.clearurlskt

import ClearURLResource
import com.google.gson.JsonObject
import fe.gson.extensions.obj
import fe.gson.extensions.parseReaderAs
import fe.gson.extensions.parseStringAs
import java.io.InputStream

object ClearURLLoader {
    fun loadBuiltInClearURLProviders() = loadClearUrlsProviders(loadClearUrlsJson(ClearURLResource.getBuiltInClearUrlsJson()!!).obj("providers"))
    fun loadClearUrlsJson(inputStream: InputStream) = inputStream.use { parseReaderAs<JsonObject>(it.reader()) }
    fun loadClearUrlsJson(text: String) = parseStringAs<JsonObject>(text)
}
