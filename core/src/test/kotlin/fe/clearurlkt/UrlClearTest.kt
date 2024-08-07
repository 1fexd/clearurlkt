package fe.clearurlkt

import fe.clearurlskt.ClearURL.clearUrl
import fe.clearurlskt.ClearURLLoader
import kotlin.test.Test
import kotlin.test.assertEquals

class UrlClearTest {
    private val providers = ClearURLLoader.loadBuiltInClearURLProviders()

    @Test
    fun testUrlClear() {
        mapOf(
            "https://deezer.com/track/891177062" to "https://deezer.com/track/891177062?utm_source=deezer",
            "https://DEEZER.com/track/891177062" to "https://DEEZER.com/track/891177062?utm_source=deezer",
            "HTTPS://DEEZER.com/TRACK/891177062" to "HTTPS://DEEZER.com/TRACK/891177062?UTM_SOURCE=deezer",
            "https://pypi.org/project/Unalix" to "https://www.google.com/url?q=https://pypi.org/project/Unalix",
            "https://de.statista.com/infografik/amp/22496/anzahl-der-gesamten-positiven-corona-tests-und-positivenrate/" to "https://www.google.com/amp/s/de.statista.com/infografik/amp/22496/anzahl-der-gesamten-positiven-corona-tests-und-positivenrate/",
            "https://www.amazon.com/gp/B08CH7RHDP" to "https://www.amazon.com/gp/B08CH7RHDP/ref=as_li_ss_tl",
            "http://0.0.0.0/" to "http://0.0.0.0/?utm_source=local",
            "https://myaccount.google.com/?utm_source=google" to "https://myaccount.google.com/?utm_source=google",
            "http://example.com/?p1=&p2=" to "http://example.com/?p1=&p2=",
            "http://example.com/?p1=value&p1=othervalue" to "http://example.com/?p1=value&p1=othervalue",
            "http://example.com/?&&&&" to "http://example.com/?&&&&",
            "https://docs.julialang.org/en/v1/stdlib/REPL/#Key-bindings" to "https://docs.julialang.org/en/v1/stdlib/REPL/#Key-bindings",
            "https://www.instagram.com/runabyte" to "https://www.instagram.com/runabyte?igshid=XkEaRAZ3L3X%3D",
            "https://twitter.com/DelusionPosting/status/1630991327381929987" to "https://twitter.com/DelusionPosting/status/1630991327381929987?t=AP1I12BA7jOlee95KLpgqX&s=19",
            "https://example.com/#%23" to "https://example.com/##",
            "https://example.com/??" to "https://example.com/??",
            "https://example.com/#xxxxxxxxxx%23" to "https://example.com/#xxxxxxxxxx#",
            "https://social.tchncs.de/oauth/authorize?client_id=CLIENT_LEL&redirect_uri=urn:ietf:wg:oauth:2.0:oob&response_type=code&scope=read" to "https://social.tchncs.de/oauth/authorize?client_id=CLIENT_LEL&redirect_uri=urn:ietf:wg:oauth:2.0:oob&response_type=code&scope=read",
            "https://minecrafthelp.zendesk.com/requests/9999999999999999999/satisfaction/new/asfdasfasfasfasdasdasdasdasd?locale=1&intention=16" to "https://www.google.com/url?q=https://minecrafthelp.zendesk.com/requests/9999999999999999999/satisfaction/new/asfdasfasfasfasdasdasdasdasd?locale%3D1%26intention%3D16&source=gmail&ust=999999999999999999999&usg=asdafsasfasfasfasfaf",
            "https://open.spotify.com/playlist/fuck_you_spotify?pt=trash_app" to "https://open.spotify.com/playlist/fuck_you_spotify?si=lol&pt=trash_app",
            "https://bit.ly/3tTxAv4" to "https://lm.facebook.com/l.php?u=https%3A%2F%2Fbit.ly%2F3tTxAv4%3Ffbclid%3DIwAR2BRY7IuBvxCV8OI74v-lWKb0RZAHEmVjfGn2OCRLYJpdrfz2Ow47UqLJc&h=AT1vCA39uUU-mV4NAf7NyueUILrXGPNjF4c1I_YVs6rdBcifbHQI5pVII5W2X4C1ORr01CKJf4VcTV4Mg9xMuz63vj6F-KHHB3OMDASwnc9lEAM8OlU51rVsJNWB_gUhj3K1s401VC4l4_h3E_5R",
            "https://ingka.page.link/?link=https://order.ikea.com/at/history/%23/lookup?orderId%3Dfuck_you_ikea%26lid%3Dyeeeet&apn=com.ingka.ikea.app&afl=https://order.ikea.com/at/de/purchases/ikea_sucks/?lid=yeeeet&ibi=com.ingka.ikea.app&ifl=https://order.ikea.com/at/de/purchases/ikea_sucks/?lid=yeeeet&ofl=https://order.ikea.com/at/de/purchases/ikea_sucks/?lid=yeeeet&imv=14.88.69&amv=8869"
                    to "https://ingka.page.link/?link=https://order.ikea.com/at/history/%23/lookup?orderId%3Dfuck_you_ikea%26lid%3Dyeeeet&apn=com.ingka.ikea.app&afl=https://order.ikea.com/at/de/purchases/ikea_sucks/?lid=yeeeet&ibi=com.ingka.ikea.app&ifl=https://order.ikea.com/at/de/purchases/ikea_sucks/?lid=yeeeet&ofl=https://order.ikea.com/at/de/purchases/ikea_sucks/?lid=yeeeet&imv=14.88.69&amv=8869",
            "https://m.facebook.com/v16.0/dialog/oauth?cct_prefetching=0&client_id=cringe&cbt=lol&e2e=xd&ies=0&sdk=android-16.1.3&sso=chrome_custom_tab&nonce=jabro&scope=openid%2Cpublic_profile%2Cemail&state=Lule&code_challenge_method=S256&default_audience=friends&login_behavior=NATIVE_WITH_FALLBACK&redirect_uri=fbconnect%3A%2F%2Fnö&auth_type=rerequest&response_type=id_token%2Ctoken%2Csigned_request%2Cgraph_domain&return_scopes=true&code_challenge=facebook_sucks"
                    to "https://m.facebook.com/v16.0/dialog/oauth?cct_prefetching=0&client_id=cringe&cbt=lol&e2e=xd&ies=0&sdk=android-16.1.3&sso=chrome_custom_tab&nonce=jabro&scope=openid%2Cpublic_profile%2Cemail&state=Lule&code_challenge_method=S256&default_audience=friends&login_behavior=NATIVE_WITH_FALLBACK&redirect_uri=fbconnect%3A%2F%2Fnö&auth_type=rerequest&response_type=id_token%2Ctoken%2Csigned_request%2Cgraph_domain&return_scopes=true&code_challenge=facebook_sucks",
            "https://analyticsindiamag.com/iit-bombay-joins-ibm-meta-to-form-ai-alliance-challenging-openai-google-amazon-microsoft/" to "https://analyticsindiamag.com/iit-bombay-joins-ibm-meta-to-form-ai-alliance-challenging-openai-google-amazon-microsoft/?utm_source=rss&utm_medium=rss&utm_campaign=iit-bombay-joins-ibm-meta-to-form-ai-alliance-challenging-openai-google-amazon-microsoft",
            "https://www.google.com/search?q=%E4%B8%AD%E6%96%87" to "https://www.google.com/search?q=%E4%B8%AD%E6%96%87",
            "https://myanimelist.net/v1/oauth2/authorize?client_id=xd&code_challenge=lule123&response_type=code" to "https://myanimelist.net/v1/oauth2/authorize?client_id=xd&code_challenge=lule123&response_type=code",
            "https://accounts.nintendo.com/connect/1.0.0/authorize?state=aGb6tyEy1VlsoOh96xsttpK2jg7FlqM0EmI5ovlSDrOEDIkSz7&redirect_uri=npfd123dc2adf715a15%3A%2F%2Fauth&client_id=d123dc2adf715a15&lang=en-US&scope=openid+user+user.birthday+user%3AanyUsers%3Apublic+mission+missionStatus+missionCompletion+members%3Aauthenticate+userGift%3Areceive+pointWallet+rewardStatus+rewardExchange%3Acreate&response_type=session_token_code&session_token_code_challenge=SPD1LQJgsnzwrjn54g3DYZZ96sEWAYmLVhEUpLDoUw7&session_token_code_challenge_method=S256" to "https://accounts.nintendo.com/connect/1.0.0/authorize?state=aGb6tyEy1VlsoOh96xsttpK2jg7FlqM0EmI5ovlSDrOEDIkSz7&redirect_uri=npfd123dc2adf715a15%3A%2F%2Fauth&client_id=d123dc2adf715a15&lang=en-US&scope=openid+user+user.birthday+user%3AanyUsers%3Apublic+mission+missionStatus+missionCompletion+members%3Aauthenticate+userGift%3Areceive+pointWallet+rewardStatus+rewardExchange%3Acreate&response_type=session_token_code&session_token_code_challenge=SPD1LQJgsnzwrjn54g3DYZZ96sEWAYmLVhEUpLDoUw7&session_token_code_challenge_method=S256"
        ).forEach { (expected, input) ->
            println("Expected: $expected")
            assertEquals(expected, clearUrl(input, providers, System.out))
        }
    }

    @Test
    fun test() {
        val link =
            "https://maps.google.com/maps?hl=zh-HK&ram_mb=7362&aos=10&vpa=1&cct_optout=1&ampcct=6167&lns_as=1&cs=0&q=%E5%8F%B0%E5%A0%B4&padt=117&rdid=8e79aa71-d72b-4d62-9d0b-49a8abfbdff2&client=ms-android-samsung-rvo1&um=1&ie=UTF-8"
        val cleared = clearUrl(link, providers, System.out)

        println(cleared)
    }
}
