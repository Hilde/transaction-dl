package info.hildegynoid.transaction.ui

import info.hildegynoid.transaction.client.HttpClient
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.DatePicker
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.stage.DirectoryChooser
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.time.LocalDate
import java.util.concurrent.Executors

class Controller : KoinComponent {

    private val logger = KotlinLogging.logger {}

    private val httpClient: HttpClient by inject()

    private val executorService = Executors.newSingleThreadExecutor()

    @FXML
    private lateinit var username: TextField
    @FXML
    private lateinit var password: PasswordField
    @FXML
    private lateinit var loginButton: Button
    @FXML
    private lateinit var startDatePicker: DatePicker
    @FXML
    private lateinit var endDatePicker: DatePicker
    @FXML
    private lateinit var downloadButton: Button
    @FXML
    private lateinit var status: Label

    @FXML
    fun initialize() {
        val now = LocalDate.now()
        startDatePicker.value = now.minusMonths(1)
        endDatePicker.value = now
    }

    fun shutdown() {
        executorService.shutdown()
    }

    @FXML
    fun loginButtonOnClick(actionEvent: ActionEvent) {
        if (username.text.isBlank() || password.text.isBlank()) {
            status.text = "Username and password needed"
            return
        }

        username.isDisable = true
        password.isDisable = true
        loginButton.isDisable = true
        startDatePicker.isDisable = true
        endDatePicker.isDisable = true
        downloadButton.isDisable = true

        // Create task
        val user = username.text
        val pass = password.text
        val task = object : Task<Boolean>() {
            override fun call(): Boolean =
                try {
                    updateMessage("Logging in...")
                    httpClient.login(user, pass)
                    updateMessage("Login successfully")
                    true
                } catch (e: Exception) {
                    logger.error(e) { "Login failed." }
                    updateMessage("Login failed: ${e.message}")
                    false
                }
        }
        // Bind
        status.textProperty().bind(task.messageProperty())
        // Enqueue
        executorService.submit(task)

        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
            if (task.value) {
                downloadButton.isDisable = !task.value
                startDatePicker.isDisable = !task.value
                endDatePicker.isDisable = !task.value
            } else {
                username.isDisable = false
                password.isDisable = false
                loginButton.isDisable = false
            }
        }
    }

    @FXML
    fun downloadButtonOnClick(actionEvent: ActionEvent) {
        downloadButton.isDisable = true

        val dirChooser = DirectoryChooser()
        dirChooser.initialDirectory = File(System.getProperty("user.home"))
        dirChooser.title = "Choose directory to save file"
        val dir = dirChooser.showDialog(null)
        if (dir == null) {
            downloadButton.isDisable = false
            return
        }

        val startDate = startDatePicker.value
        val endDate = endDatePicker.value

        // Create task
        val task = object : Task<Boolean>() {
            override fun call(): Boolean {
                try {
                    updateMessage("Download from $startDate to $endDate")
                    httpClient.download(startDate, endDate, HttpClient.FileType.XML, dir.toPath())
                    updateMessage("Download successfully")
                } catch (e: Exception) {
                    logger.error(e) { "Login failed." }
                    updateMessage("Download failed: ${e.message}")
                    return false
                }
                return true
            }
        }

        // Bind
        status.textProperty().bind(task.messageProperty())
        // Enqueue
        executorService.submit(task)

        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
            username.isDisable = false
            password.isDisable = false
            loginButton.isDisable = false
            downloadButton.isDisable = false
        }
    }
}
