package fe.clearurlskt

import ClearURLResource
import com.google.gson.JsonObject
import fe.gson.extension.io.fromJson
import fe.gson.extension.json.`object`.asObject
import fe.gson.util.JsonIOUtil
import java.io.InputStream

object ClearURLLoader {
    fun loadBuiltInClearURLProviders() = loadClearUrlsProviders(
        loadClearUrlsJson(ClearURLResource.getBuiltInClearUrlsJson()!!)
            .asObject("providers")
    )

    fun loadClearUrlsJson(inputStream: InputStream): JsonObject {
        return inputStream.fromJson<JsonObject>()
    }

    fun loadClearUrlsJson(text: String): JsonObject {
        return JsonIOUtil.fromJson<JsonObject>(text)
    }
}
