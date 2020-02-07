package info.hildegynoid.transaction

import info.hildegynoid.transaction.client.HttpClient
import info.hildegynoid.transaction.client.HttpClientImpl
import info.hildegynoid.transaction.client.SecondLifeProperty
import org.koin.dsl.module

val myModule = module {
    single { SecondLifeProperty() }
    single { HttpClientImpl(get()) as HttpClient }
}
