import java.io.InputStream

object ClearURLResource {
    fun getBuiltInClearUrlsJson(name: String = "clearurls.json"): InputStream? {
        return ClearURLResource::class.java.getResourceAsStream(name)
    }
}
