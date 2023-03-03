import java.io.InputStream
object ClearUrlsResource
fun getBuiltInClearUrlsJson(name: String = "clearurls.json"): InputStream? = ClearUrlsResource::class.java.getResourceAsStream(name)
