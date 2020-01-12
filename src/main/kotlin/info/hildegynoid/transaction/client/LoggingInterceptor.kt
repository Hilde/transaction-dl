package info.hildegynoid.transaction.client

import mu.KotlinLogging
import okhttp3.Interceptor
import okhttp3.Response

class LoggingInterceptor : Interceptor {
    private val logger = KotlinLogging.logger {}

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val t1 = System.nanoTime()
        logger.trace {
            "Sending request %s on %s%n%s".format(request.url, chain.connection(), request.headers)
        }
        val response: Response = chain.proceed(request)
        val t2 = System.nanoTime()
        logger.trace {
            "Received response for %s in %.1fms%n%s".format(response.request.url, (t2 - t1) / 1e6, response.headers)
        }
        return response
    }
}
