package info.hildegynoid.transaction

import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.util.Callback
import mu.KotlinLogging
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.io.IOException

const val APPLICATION_TITLE = "Transaction downloader"

@Component
class MyApplication : Application() {
    private val logger = KotlinLogging.logger {}

    lateinit var context: ConfigurableApplicationContext

    override fun init() {
        // Run spring application
        context = SpringApplicationBuilder(Main::class.java).run()
    }

    override fun start(stage: Stage) {
        try {
            val resource = ClassPathResource("fxml/main.fxml")
            logger.debug { resource.url }
            val loader = FXMLLoader(resource.url)
            loader.controllerFactory = Callback { aClass: Class<out Any> ->
                context.getBean(aClass)
            }
            val parent = loader.load<Parent>()
            stage.scene = Scene(parent, 400.0, 400.0)
            stage.title = APPLICATION_TITLE
            stage.show()
        } catch (e: IOException) {
            logger.error(e) { "something wrong" }
            throw RuntimeException()
        }

        // val javaVersion = System.getProperty("java.version")
        // val javafxVersion = System.getProperty("javafx.version")
        // val l = Label("Hello, JavaFX $javafxVersion, running on Java $javaVersion.")
        // val scene = Scene(StackPane(l), 640.0, 480.0)
        // stage.scene = scene
        // stage.show()
    }

    override fun stop() {
        // Stop spring application
        context.close()
        Platform.exit()
    }
}
