package fe.clearurlkt

import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.clearurlskt.ClearURL.clearUrl
import fe.clearurlskt.ClearURLLoader
import fe.std.assert.assertEach
import fe.std.assert.trimMargin
import kotlin.test.Test

class UrlClearTest {

    private data class ClearUrlTest(val expected: String, val input: String)

    private val tests = listOf(
        ClearUrlTest(
            expected = "https://deezer.com/track/891177062",
            input = "https://deezer.com/track/891177062?utm_source=deezer"
        ),
        ClearUrlTest(
            expected = "https://DEEZER.com/track/891177062",
            input = "https://DEEZER.com/track/891177062?utm_source=deezer"
        ),
        ClearUrlTest(
            expected = "HTTPS://DEEZER.com/TRACK/891177062",
            input = "HTTPS://DEEZER.com/TRACK/891177062?UTM_SOURCE=deezer"
        ),
        ClearUrlTest(
            expected = "https://pypi.org/project/Unalix",
            input = "https://www.google.com/url?q=https://pypi.org/project/Unalix"
        ),
        ClearUrlTest(
            expected = "https://www.amazon.com/gp/B08CH7RHDP",
            input = "https://www.amazon.com/gp/B08CH7RHDP/ref=as_li_ss_tl"
        ),
        ClearUrlTest(
            expected = "http://0.0.0.0/",
            input = "http://0.0.0.0/?utm_source=local"
        ),
        ClearUrlTest(
            expected = "https://myaccount.google.com/?utm_source=google",
            input = "https://myaccount.google.com/?utm_source=google"
        ),
        ClearUrlTest(
            expected = "http://example.com/?&&&&",
            input = "http://example.com/?&&&&"
        ),
        ClearUrlTest(
            expected = "http://example.com/?p1=&p2=",
            input = "http://example.com/?p1=&p2="
        ),
        ClearUrlTest(
            expected = "http://example.com/?p1=othervalue",
//            expected = "http://example.com/?p1=value&p1=othervalue",
            input = "http://example.com/?p1=value&p1=othervalue"
        ),
        ClearUrlTest(
            expected = "https://example.com/#%23",
            input = "https://example.com/##"
        ),
        ClearUrlTest(
            expected = "https://example.com/?%3F",
            input = "https://example.com/??"
        ),
        ClearUrlTest(
            expected = "https://example.com/#xxxxxxxxxx%23",
            input = "https://example.com/#xxxxxxxxxx#"
        ),
        ClearUrlTest(
            expected = "https://www.instagram.com/runabyte",
            input = "https://www.instagram.com/runabyte?igshid=XkEaRAZ3L3X%3D"
        ),
        ClearUrlTest(
            expected = "https://docs.julialang.org/en/v1/stdlib/REPL/#Key-bindings",
            input = "https://docs.julialang.org/en/v1/stdlib/REPL/#Key-bindings"
        ),
        ClearUrlTest(
            expected = "https://twitter.com/DelusionPosting/status/1630991327381929987",
            input = "https://twitter.com/DelusionPosting/status/1630991327381929987?t=AP1I12BA7jOlee95KLpgqX&s=19"
        ),
        ClearUrlTest(
            expected = "https://open.spotify.com/playlist/spottifei?pt=trash_app",
            input = "https://open.spotify.com/playlist/spottifei?si=lol&pt=trash_app"
        ),
        ClearUrlTest(
            expected = "https://www.google.com/search?q=%E4%B8%AD%E6%96%87",
            input = "https://www.google.com/search?q=%E4%B8%AD%E6%96%87"
        ),
        ClearUrlTest(
            expected = "https://de.statista.com/infografik/amp/22496/anzahl-der-gesamten-positiven-corona-tests-und-positivenrate/",
            input = "https://www.google.com/amp/s/de.statista.com/infografik/amp/22496/anzahl-der-gesamten-positiven-corona-tests-und-positivenrate/"
        ),
        ClearUrlTest(
            expected = "https://myanimelist.net/v1/oauth2/authorize?client_id=xd&code_challenge=lule123&response_type=code",
            input = "https://myanimelist.net/v1/oauth2/authorize?client_id=xd&code_challenge=lule123&response_type=code",
        ),
        ClearUrlTest(
            expected = """
                |https://social.tchncs.de/oauth/authorize?client_id=CLIENT_LEL
                |&redirect_uri=urn%3Aietf%3Awg%3Aoauth%3A2.0%3Aoob&response_type=code&scope=read""".trimMargin(
                lineSeparator = ""
            ),
            input = """
                |https://social.tchncs.de/oauth/authorize?client_id=CLIENT_LEL
                |&redirect_uri=urn:ietf:wg:oauth:2.0:oob&response_type=code&scope=read""".trimMargin(lineSeparator = "")
        ),
        ClearUrlTest(
            expected = """
                |https://minecrafthelp.zendesk.com/requests/9999999999999999999/satisfaction/new/
                |asfdasfasfasfasdasdasdasdasd?locale=1&intention=16""".trimMargin(lineSeparator = ""),
            input = """
                |https://www.google.com/url?q=https://minecrafthelp.zendesk.com/requests/9999999999999999999/
                |satisfaction/new/asfdasfasfasfasdasdasdasdasd?locale%3D1%26intention%3D16&source=gmail
                |&ust=999999999999999999999&usg=asdafsasfasfasfasfaf""".trimMargin(lineSeparator = "")
        ),
        ClearUrlTest(
            expected = "https://bit.ly/3tTxAv4",
            input = """
                |https://lm.facebook.com/l.php?u=
                |https%3A%2F%2Fbit.ly%2F3tTxAv4%3Ffbclid%3DIwAR2BRY7IuBvxCV8OI74v-lWKb0RZAHEmVjfGn2OCRLYJpdrfz2Ow47UqLJc
                |&h=AT1vCA39uUU-mV4NAf7NyueUILrXGPNjF4c1I_YVs6rdBcifbHQI5pVII5W2X4C1ORr01CKJf4VcTV4Mg9xMuz63vj6F
                |-KHHB3OMDASwnc9lEAM8OlU51rVsJNWB_gUhj3K1s401VC4l4_h3E_5R""".trimMargin(lineSeparator = ""),
        ),
        ClearUrlTest(
            expected = """
                |https://ingka.page.link/?link=https%3A%2F%2Forder.ikea.com%2Fat%2Fhistory%2F%23%2Flookup%3ForderId
                |%3Dikea123%26lid%3Dyeeeet&apn=com.ingka.ikea.app
                |&afl=https%3A%2F%2Forder.ikea.com%2Fat%2Fde%2Fpurchases%2Fikea_sucks%2F%3Flid%3Dyeeeet&ibi=com.ingka.ikea.app
                |&ifl=https%3A%2F%2Forder.ikea.com%2Fat%2Fde%2Fpurchases%2Fikea_sucks%2F%3Flid%3Dyeeeet
                |&ofl=https%3A%2F%2Forder.ikea.com%2Fat%2Fde%2Fpurchases%2Fikea_sucks%2F%3Flid%3Dyeeeet&imv=1.2.3&amv=1100""".trimMargin(
                lineSeparator = ""
            ),
            input = """
                |https://ingka.page.link/?link=https://order.ikea.com/at/history/%23/lookup?orderId%3Dikea123%26lid%3Dyeeeet
                |&apn=com.ingka.ikea.app&afl=https://order.ikea.com/at/de/purchases/ikea_sucks/?lid=yeeeet&ibi=com.ingka.ikea.app
                |&ifl=https://order.ikea.com/at/de/purchases/ikea_sucks/?lid=yeeeet
                |&ofl=https://order.ikea.com/at/de/purchases/ikea_sucks/?lid=yeeeet&imv=1.2.3&amv=1100""".trimMargin(
                lineSeparator = ""
            ),
        ),
        ClearUrlTest(
            expected = """
                |https://m.facebook.com/v16.0/dialog/oauth?cct_prefetching=0&client_id=cringe&cbt=lol&e2e=xd&ies=0&sdk=android-16.1.3
                |&sso=chrome_custom_tab&nonce=jabro&scope=openid%2Cpublic_profile%2Cemail&state=Lule&code_challenge_method=S256
                |&default_audience=friends&login_behavior=NATIVE_WITH_FALLBACK&redirect_uri=fbconnect%3A%2F%2Fn%EF%BF%BD&auth_type=rerequest
                |&response_type=id_token%2Ctoken%2Csigned_request%2Cgraph_domain&return_scopes=true&code_challenge=facebook_sucks""".trimMargin(
                lineSeparator = ""
            ),
            input = """
                |https://m.facebook.com/v16.0/dialog/oauth?cct_prefetching=0&client_id=cringe&cbt=lol&e2e=xd&ies=0
                |&sdk=android-16.1.3&sso=chrome_custom_tab&nonce=jabro&scope=openid%2Cpublic_profile%2Cemail
                |&state=Lule&code_challenge_method=S256&default_audience=friends&login_behavior=NATIVE_WITH_FALLBACK
                |&redirect_uri=fbconnect%3A%2F%2Fnö&auth_type=rerequest&response_type=id_token%2Ctoken%2Csigned_request%2Cgraph_domain
                |&return_scopes=true&code_challenge=facebook_sucks""".trimMargin(lineSeparator = ""),
        ),
        ClearUrlTest(
            expected = """
                |https://analyticsindiamag.com/iit-bombay-joins-ibm-meta-to-form-ai-alliance-challenging-openai-google-amazon-microsoft/""".trimMargin(
                lineSeparator = ""
            ),
            input = """
                |https://analyticsindiamag.com/iit-bombay-joins-ibm-meta-to-form-ai-alliance-challenging-openai-google-amazon-microsoft/
                |?utm_source=rss&utm_medium=rss
                |&utm_campaign=iit-bombay-joins-ibm-meta-to-form-ai-alliance-challenging-openai-google-amazon-microsoft""".trimMargin(
                lineSeparator = ""
            ),
        ),
        ClearUrlTest(
            expected = """
                |https://accounts.nintendo.com/connect/1.0.0/authorize?state=aGb6tyEy1VlsoOh96xsttpK2jg7FlqM0EmI5ovlSDrOEDIkSz7
                |&redirect_uri=npfd123dc2adf715a15%3A%2F%2Fauth&client_id=d123dc2adf715a15&lang=en-US
                |&scope=openid%2Buser%2Buser.birthday%2Buser%3AanyUsers%3Apublic%2Bmission%2BmissionStatus%2BmissionCompletion
                |%2Bmembers%3Aauthenticate%2BuserGift%3Areceive%2BpointWallet%2BrewardStatus%2BrewardExchange%3Acreate
                |&response_type=session_token_code&session_token_code_challenge=SPD1LQJgsnzwrjn54g3DYZZ96sEWAYmLVhEUpLDoUw7
                |&session_token_code_challenge_method=S256""".trimMargin(lineSeparator = ""),
            input = """
                |https://accounts.nintendo.com/connect/1.0.0/authorize?state=aGb6tyEy1VlsoOh96xsttpK2jg7FlqM0EmI5ovlSDrOEDIkSz7
                |&redirect_uri=npfd123dc2adf715a15%3A%2F%2Fauth&client_id=d123dc2adf715a15&lang=en-US
                |&scope=openid+user+user.birthday+user%3AanyUsers%3Apublic+mission+missionStatus+missionCompletion+members
                |%3Aauthenticate+userGift%3Areceive+pointWallet+rewardStatus+rewardExchange%3Acreate&response_type=session_token_code
                |&session_token_code_challenge=SPD1LQJgsnzwrjn54g3DYZZ96sEWAYmLVhEUpLDoUw7&session_token_code_challenge_method=S256""".trimMargin(
                lineSeparator = ""
            )
        )
    )

    private val providers = ClearURLLoader.loadBuiltInClearURLProviders()

    private fun runTest(input: String): String {
        return clearUrl(input, providers)
    }

    @Test
    fun `blank encoding`() {
        val tests = listOf(
            ClearUrlTest(
                expected = "https://www.google.com/search?q=never%20gonna%20give%20you%20up",
                input = "https://www.google.com/search?q=never%20gonna%20give%20you%20up"
            ),
            ClearUrlTest(
                expected = "https://www.google.com/search?q=never%20gonna%20give%20you%20up",
                input = "https://www.google.com/search?q=never+gonna+give+you+up"
            )
        )

        assertEach(tests) { (expected, input) ->
            assertThat(runTest(input)).isEqualTo(expected)
        }
    }

    @Test
    fun test() {
        assertEach(tests) { (expected, input) ->
            val result = clearUrl(input, providers, null)
            assertThat(result).isEqualTo(expected)
        }
    }
}
