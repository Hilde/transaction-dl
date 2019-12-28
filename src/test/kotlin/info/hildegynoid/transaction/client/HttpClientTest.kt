package info.hildegynoid.transaction.client

import info.hildegynoid.transaction.MyApplication
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import java.nio.file.Paths
import java.time.LocalDate

@SpringBootTest
@ContextConfiguration(classes = [MyApplication::class])
@Configuration
@Import(OkHttpConfig::class)
class HttpClientTest {

    private val server = MockWebServer()

    @Autowired
    lateinit var builder: OkHttpClient.Builder

    private lateinit var httpClient: HttpClient

    fun property() = SecondLifeProperty(
        loginUrl = server.url("/login").toString(),
        loginSubmitUrl = server.url("/loginsubmit").toString(),
        openIdAuthorizationUrl = server.url("/auth/oid_return").toString(),
        transactionHistoryUrl = server.url("/transaction_history").toString(),
        transactionDownloadUrl = server.url("/get_transaction_history_csv").toString(),
        username = "dummy.account",
        password = "dummy.password"
    )

    @BeforeEach
    fun setup() {
        httpClient = HttpClient(builder, property())
        // server.start()
    }

    @Test
    fun download() {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", "text/csv")
            .setHeader("Content-Disposition", "attachment; filename=\"SL-transactions12_01-12_15.csv\"")
            .setBody(
                "id,Type,Description,Debit,Credit,Time,Resident/Group,Ending Balance,Region,Order ID"
            )
        server.enqueue(mockResponse)

        httpClient.download(LocalDate.now(), LocalDate.now(), HttpClient.FileType.CSV, Paths.get("./"))
    }
}
