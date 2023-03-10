import fe.clearurlskt.ClearURLLoader
import fe.clearurlskt.clearUrl
import kotlin.test.Test
import kotlin.test.assertEquals

class UrlClearTest {
    @Test
    fun testUrlClear() {
        val providers = ClearURLLoader.loadBuiltInClearURLProviders()


        mapOf(
            "https://deezer.com/track/891177062" to "https://deezer.com/track/891177062?utm_source=deezer",
            "https://pypi.org/project/Unalix" to "https://www.google.com/url?q=https://pypi.org/project/Unalix",
            "https://de.statista.com/infografik/amp/22496/anzahl-der-gesamten-positiven-corona-tests-und-positivenrate/" to "https://www.google.com/amp/s/de.statista.com/infografik/amp/22496/anzahl-der-gesamten-positiven-corona-tests-und-positivenrate/",
//            "http://www.rightstufanime.com/search?keywords=So%20I'm%20a%20Spider%20So%20What%3F" to "http://www.shareasale.com/r.cfm?u=1384175&b=866986&m=65886&afftrack=&urllink=www.rightstufanime.com%2Fsearch%3Fkeywords%3DSo%20I%27m%20a%20Spider%20So%20What%3F",
            "https://www.amazon.com/gp/B08CH7RHDP" to "https://www.amazon.com/gp/B08CH7RHDP/ref=as_li_ss_tl",
            "http://0.0.0.0/" to "http://0.0.0.0/?utm_source=local",
//            "https://natura.com.br/p/2458" to "https://natura.com.br/p/2458?consultoria=promotop",
            "https://myaccount.google.com/?utm_source=google" to "https://myaccount.google.com/?utm_source=google",
//            "http://g.co/" to "http://clickserve.dartsearch.net/link/click?ds_dest_url=http://g.co/",
            "http://example.com/" to "http://example.com/?p1=&p2=",
            "http://example.com/?p1=value" to "http://example.com/?p1=value&p1=othervalue",
            "http://example.com/" to "http://example.com/?&&&&",
            "https://docs.julialang.org/en/v1/stdlib/REPL/#Key-bindings" to "https://docs.julialang.org/en/v1/stdlib/REPL/#Key-bindings",
            "https://www.instagram.com/runabyte" to "https://www.instagram.com/runabyte?igshid=XkEaRAZ3L3X%3D",
            "https://twitter.com/DelusionPosting/status/1630991327381929987" to "https://twitter.com/DelusionPosting/status/1630991327381929987?t=AP1I12BA7jOlee95KLpgqX&s=19"
        ).forEach { (expected, input) ->
            println("Expected: $expected")
            assertEquals(expected, clearUrl(input, providers, true))
        }

    }
}
