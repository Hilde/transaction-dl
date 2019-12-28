package info.hildegynoid.transaction.client

import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.CookieManager
import java.net.CookiePolicy

@Configuration
class OkHttpConfig {
    @Bean
    fun okHttpBuilder(): OkHttpClient.Builder {
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

        return OkHttpClient.Builder()
            .addInterceptor(UserAgentInterceptor())
            .addNetworkInterceptor(LoggingInterceptor())
            .cookieJar(JavaNetCookieJar(cookieManager))
    }
}
