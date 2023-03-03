import java.io.InputStream
object Resource
fun getBuiltInClearUrlsJson(name: String = "clearurls.json"): InputStream? = Resource::class.java.getResourceAsStream(name)
