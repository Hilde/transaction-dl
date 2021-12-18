package info.hildegynoid.transaction

import info.hildegynoid.transaction.ui.Controller
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class MyApplication : Application() {

    companion object {
        const val TITLE = "Transaction DL"
        const val NAME = "transaction-dl"
        const val VERSION = "0.4.0"
    }

    override fun start(stage: Stage) {
        val resource = javaClass.classLoader.getResource("fxml/main.fxml")
        val loader = FXMLLoader(resource)
        val parent = loader.load<Parent>()
        stage.scene = Scene(parent, 450.0, 400.0)
        stage.title = "$TITLE Version.$VERSION"
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
}
