package fe.clearurlskt

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.io.InputStream

fun loadClearUrlsJson(inputStream: InputStream) = loadClearUrlsJson(inputStream.use { JsonParser.parseReader(it.reader()) })
fun loadClearUrlsJson(text: String) = loadClearUrlsJson(JsonParser.parseString(text))

private fun loadClearUrlsJson(element: JsonElement) = element.asJsonObject.getAsJsonObject("providers")
