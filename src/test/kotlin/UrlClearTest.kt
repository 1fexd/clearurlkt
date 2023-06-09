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
            "https://www.amazon.com/gp/B08CH7RHDP" to "https://www.amazon.com/gp/B08CH7RHDP/ref=as_li_ss_tl",
            "http://0.0.0.0/" to "http://0.0.0.0/?utm_source=local",
            "https://myaccount.google.com/?utm_source=google" to "https://myaccount.google.com/?utm_source=google",
            "http://example.com/" to "http://example.com/?p1=&p2=",
            "http://example.com/?p1=value" to "http://example.com/?p1=value&p1=othervalue",
            "http://example.com/" to "http://example.com/?&&&&",
            "https://docs.julialang.org/en/v1/stdlib/REPL/#Key-bindings" to "https://docs.julialang.org/en/v1/stdlib/REPL/#Key-bindings",
            "https://www.instagram.com/runabyte" to "https://www.instagram.com/runabyte?igshid=XkEaRAZ3L3X%3D",
            "https://twitter.com/DelusionPosting/status/1630991327381929987" to "https://twitter.com/DelusionPosting/status/1630991327381929987?t=AP1I12BA7jOlee95KLpgqX&s=19",
            "https://example.com/##" to "https://example.com/##",
            "https://example.com/" to "https://example.com/??",
            "https://example.com/#xxxxxxxxxx#" to "https://example.com/#xxxxxxxxxx#",
            "https://social.tchncs.de/oauth/authorize?client_id=CLIENT_LEL&redirect_uri=urn:ietf:wg:oauth:2.0:oob&response_type=code&scope=read" to "https://social.tchncs.de/oauth/authorize?client_id=CLIENT_LEL&redirect_uri=urn:ietf:wg:oauth:2.0:oob&response_type=code&scope=read",
            "https://minecrafthelp.zendesk.com/requests/9999999999999999999/satisfaction/new/asfdasfasfasfasdasdasdasdasd?locale=1&intention=16" to "https://www.google.com/url?q=https://minecrafthelp.zendesk.com/requests/9999999999999999999/satisfaction/new/asfdasfasfasfasdasdasdasdasd?locale%3D1%26intention%3D16&source=gmail&ust=999999999999999999999&usg=asdafsasfasfasfasfaf",
            "https://open.spotify.com/playlist/fuck_you_spotify?pt=trash_app" to "https://open.spotify.com/playlist/fuck_you_spotify?si=lol&pt=trash_app",
            "https://bit.ly/3tTxAv4" to "https://lm.facebook.com/l.php?u=https%3A%2F%2Fbit.ly%2F3tTxAv4%3Ffbclid%3DIwAR2BRY7IuBvxCV8OI74v-lWKb0RZAHEmVjfGn2OCRLYJpdrfz2Ow47UqLJc&h=AT1vCA39uUU-mV4NAf7NyueUILrXGPNjF4c1I_YVs6rdBcifbHQI5pVII5W2X4C1ORr01CKJf4VcTV4Mg9xMuz63vj6F-KHHB3OMDASwnc9lEAM8OlU51rVsJNWB_gUhj3K1s401VC4l4_h3E_5R"
        ).forEach { (expected, input) ->
            println("Expected: $expected")
            assertEquals(expected, clearUrl(input, providers, true))
        }
    }
}
