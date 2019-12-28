package info.hildegynoid.transaction.client

import mu.KotlinLogging
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class HttpClient(
    okHttpBuilder: OkHttpClient.Builder,
    val property: SecondLifeProperty
) {
    private val logger = KotlinLogging.logger {}

    private val httpClient: OkHttpClient = okHttpBuilder.build()

    enum class FileType(val param: String) {
        CSV("csv"),
        XML("xml"),
        XLS("xls")
    }

    fun login(username: String, password: String) {
        logger.info { "Login start" }

        val formBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .add("stay_logged_in", "stay_logged_in")
            .add("Submit", "")
            .add("return_to", "https://www.secondlife.com")
            .add("previous_language", "en_US")
            .add("language", "en_US")
            .add("show_join", "True")
            .build()
        val request = Request.Builder()
            .url(property.loginSubmitUrl)
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

        logger.info { "Start OpenID AuthZ request" }
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
            val form = doc.select("form#openid_message").first()
            val action = form.attr("action")
            val message = hashMapOf<String, String>()
            form.select("input").forEach {
                val name = it.attr("name")
                val value = it.attr("value")
                message[name] = value
            }

            if (action != null && message.isNotEmpty()) {
                logger.info { "start OpenID AuthZ submit" }
                val body3Builder = FormBody.Builder()
                message.forEach { (t, u) -> body3Builder.add(t, u) }
                val request3 = Request.Builder()
                    .url(action)
                    .post(body3Builder.build())
                    .build()
                logger.info { "OpenIdAuthZSubmit" }
                val noRedirectClient = httpClient.newBuilder()
                    .build()
                noRedirectClient.newCall(request3).execute().use { response3 ->
                    if (!response3.isSuccessful) throw IOException("Unexpected code $response3")

                    logger.info { "Login successfully" }
                }
            }
        }
    }

    fun download(start: LocalDate, end: LocalDate, fileType: FileType = FileType.XML, outputDir: Path) {
        logger.info { "start download" }

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
        logger.debug { html }
        val errorMessage = form.selectFirst("div.error").text()
        return !errorMessage.contains("the username or password that you entered were incorrect")
    }
}
