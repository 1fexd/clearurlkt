package fe.clearurlskt

import fe.std.result.Failure
import fe.std.uri.Url

sealed interface ClearUrlOperation

sealed class Modification(val provider: String, val input: String, val result: String) : ClearUrlOperation {
    class ParameterRemoval(
        provider: String,
        input: String,
        result: String,
        val fields: MutableSet<String>,
        val fragment: MutableSet<String>,
    ) : Modification(provider, input, result)

    class RawRule(
        provider: String,
        input: String,
        result: String,
        val regex: Regex,
    ) : Modification(provider, input, result)

    class Redirection(
        provider: String,
        input: String,
        result: String,
        val regex: Regex,
    ) : Modification(provider, input, result)
}

data class Exception(val provider: String, val url: String, val regex: Regex) : ClearUrlOperation
data class ParseFailure(val provider: String, val url: String, val parseResult: Failure<Url>) : ClearUrlOperation
