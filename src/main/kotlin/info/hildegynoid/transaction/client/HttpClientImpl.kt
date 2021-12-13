package info.hildegynoid.transaction.client

import mu.KotlinLogging
import okhttp3.FormBody
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HttpClientImpl(private val property: SecondLifeProperty) : HttpClient {

    private val logger = KotlinLogging.logger {}

    private val httpClient: OkHttpClient

    init {
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

        httpClient = OkHttpClient.Builder()
            .addInterceptor(UserAgentInterceptor())
            .addNetworkInterceptor(LoggingInterceptor())
            .cookieJar(JavaNetCookieJar(cookieManager))
            .build()
    }

    private fun getCsrfToken(): String {
        val request = Request.Builder()
            .url(property.loginUrl)
            .get()
            .build()

        return httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val body = response.body?.string() ?: throw Exception("No document")

            Jsoup.parse(body)
                .select("#loginform input[name=\"csrfmiddlewaretoken\"]")
                .first()?.attr("value") ?: throw Exception("No csrf token")
        }
    }

    override fun login(username: String, password: String) {
        logger.info { "Login start" }

        val csrfToken = getCsrfToken()

        val formBody = FormBody.Builder()
            .add("csrfmiddlewaretoken", csrfToken)
            .add("username", username)
            .add("password", password)
            .add("Submit", "")
            .add("return_to", "https://www.secondlife.com")
            .add("previous_language", "en_US")
            .add("language", "en_US")
            .add("stay_logged_in", "stay_logged_in")
            .add("show_join", "True")
            .add("from_amazon", "")
            .build()
        val request = Request.Builder()
            .url(property.loginSubmitUrl)
            .addHeader("Referer", "https://id.secondlife.com/")
            .post(formBody)
            .build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            if (response.request.url.toString() == property.loginSubmitUrl) {
                // May be login failed
                if (!checkLogin(response.body?.string() ?: "")) {
                    throw Exception("Cannot login. Check your username of password.")
                }
            }
        }

        logger.debug { "Start OpenID AuthZ request" }
        val request2 = Request.Builder()
            .url(property.openIdAuthorizationUrl + username.replace(" ", ".").toLowerCase())
            .get()
            .build()
        httpClient.newCall(request2).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            if (response.body == null) {
                throw Exception("No document")
            }
            val body = response.body!!.string()

            val doc = Jsoup.parse(body)
            doc.select("form#openid_message").first()?.let { form ->
                val action = form.attr("action")
                val message = hashMapOf<String, String>()
                form.select("input").forEach {
                    val name = it.attr("name")
                    val value = it.attr("value")
                    message[name] = value
                }

                logger.debug { "Start OpenID AuthZ submit" }
                val body3Builder = FormBody.Builder()
                message.forEach { (t, u) -> body3Builder.add(t, u) }
                val request3 = Request.Builder()
                    .url(action)
                    .post(body3Builder.build())
                    .build()
                val noRedirectClient = httpClient.newBuilder()
                    .build()
                noRedirectClient.newCall(request3).execute().use { response3 ->
                    if (!response3.isSuccessful) throw IOException("Unexpected code $response3")
                    logger.info { "Login successfully" }
                }
            }
        }
    }

    override fun download(start: LocalDate, end: LocalDate, fileType: HttpClient.FileType, outputDir: Path) {
        logger.info { "Start download from $start to $end" }

        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val url =
            property.transactionDownloadUrl + "?startDate=%s&endDate=%s&omit_zero_amounts=false&lang=en-US&%s=1".format(
                start.format(formatter), end.format(formatter), fileType.param
            )
        val request2 = Request.Builder()
            .url(url)
            .get()
            .build()
        val client = httpClient.newBuilder()
            .followRedirects(false)
            .build()
        client.newCall(request2).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            // output to file
            val regex = "filename=\"?([^\"]+)".toRegex()
            val match = regex.find(response.headers["Content-Disposition"]!!)
            val filename = match?.groupValues?.get(1) ?: "SL-transactions"
            val path = outputDir.resolve(filename)
            logger.info { "Download to $path" }
            Files.write(path, response.body!!.bytes())
        }
    }

    private fun checkLogin(html: String): Boolean {
        if (html.isEmpty()) {
            return false
        }
        val doc = Jsoup.parse(html)
        val form = doc.selectFirst("form#loginform") ?: return false
        logger.trace { html }
        val errorMessage = form.selectFirst("div.error")?.text() ?: ""
        return !errorMessage.contains("the username or password that you entered were incorrect")
    }
}
