package fe.clearurlskt.loader

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import fe.clearurlskt.Resource
import fe.clearurlskt.provider.Provider
import fe.clearurlskt.provider.ProviderSerializer
import fe.signify.PublicKey
import fe.signify.Signature
import java.io.InputStream

interface ClearURLConfigLoader {
    fun load(): Result<List<Provider>?>
}

object BundledClearURLConfigLoader : ClearURLConfigLoader {
    private const val file = "clearurls.json"
    private val bundledClass = Resource::class.java

    private val url by lazy {
        bundledClass.getResource(file)
            ?: ClassLoader.getSystemResource("${bundledClass.`package`.name.replace(".", "/")}/$file")
    }

    override fun load(): Result<List<Provider>?> {
        return runCatching {
            url?.openStream()?.let { ProviderSerializer.handle(it) }
        }
    }
}

class StreamClearURLConfigLoader(private val stream: InputStream) : ClearURLConfigLoader {
    override fun load(): Result<List<Provider>?> {
        return runCatching {
            ProviderSerializer.handle(stream)
        }
    }
}

object RemoteLoader {
    val gson: Gson = GsonBuilder().create()
    val publicKey = PublicKey.fromString("RWQazSQ29JJBtHn/Vze0iWHWGlkMUlKFQLOt2EdbTo4ToTx40uV8r8N/")

    inline fun <reified T> parseIfValid(fileStream: InputStream, signatureStream: InputStream): T? {
        val fileContent = fileStream.bufferedReader().readText()

        val signatureContent = signatureStream.bufferedReader().readLines()
        // TODO: Catch
        val signature = Signature.fromString(signatureContent.singleOrNull() ?: signatureContent[1])

        return runCatching {
            publicKey.verify(signature, fileContent.toByteArray())
            gson.fromJson(fileContent, T::class.java)
        }.getOrNull()
    }
}



