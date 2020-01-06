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
import org.springframework.boot.info.BuildProperties
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

const val APPLICATION_TITLE = "Transaction downloader"

@Component
class MyApplication : Application() {
    private val logger = KotlinLogging.logger {}

    private lateinit var buildProperties: BuildProperties

    lateinit var context: ConfigurableApplicationContext

    override fun init() {
        // Run spring application
        context = SpringApplicationBuilder(Main::class.java).run()
        buildProperties = context.getBean(BuildProperties::class.java)
    }

    override fun start(stage: Stage) {
        val resource = ClassPathResource("fxml/main.fxml")
        logger.debug { resource.url }
        val loader = FXMLLoader(resource.url)
        loader.controllerFactory = Callback { aClass: Class<out Any> ->
            context.getBean(aClass)
        }
        val parent = loader.load<Parent>()
        stage.scene = Scene(parent, 400.0, 400.0)
        stage.title = APPLICATION_TITLE + " Version." + buildProperties.version
        stage.show()

        stage.showingProperty().addListener { observable, oldValue, newValue ->
            if (oldValue == true && newValue == false) {
                context.close()
                Platform.exit()
            }
        }
    }

    override fun stop() {
        // Stop spring application
        context.close()
        Platform.exit()
    }
}
