package info.hildegynoid.transaction

import info.hildegynoid.transaction.client.HttpClient
import info.hildegynoid.transaction.client.HttpClientImpl
import info.hildegynoid.transaction.client.SecondLifeProperty
import info.hildegynoid.transaction.ui.Controller
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import org.koin.Logger.slf4jLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.dsl.module
import org.slf4j.bridge.SLF4JBridgeHandler

const val APPLICATION_TITLE = "Transaction DL"
const val APPLICATION_VERSION = "0.1.2"

class MyApplication : Application() {

    private val module: Module = module {
        single { SecondLifeProperty() }
        single { HttpClientImpl(get()) as HttpClient }
    }

    override fun init() {
        // jul to slf4j
        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()

        startKoin {
            slf4jLogger(Level.INFO)
            modules(module)
        }
    }

    override fun start(stage: Stage) {
        val resource = javaClass.classLoader.getResource("fxml/main.fxml")
        val loader = FXMLLoader(resource)
        val parent = loader.load<Parent>()
        stage.scene = Scene(parent, 450.0, 400.0)
        stage.title = APPLICATION_TITLE + " Version." + getVersion()
        stage.show()

        val controller = loader.getController<Controller>()

        stage.showingProperty().addListener { _, oldValue, newValue ->
            if (oldValue == true && newValue == false) {
                controller.shutdown()
                Platform.exit()
            }
        }
    }

    override fun stop() {
        Platform.exit()
    }

    private fun getVersion(): String? =
        APPLICATION_VERSION
        //javaClass.`package`.implementationVersion
}
