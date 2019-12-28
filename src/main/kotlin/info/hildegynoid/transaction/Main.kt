package info.hildegynoid.transaction

import javafx.application.Application
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Main

fun main(args: Array<String>) {
    Application.launch(MyApplication::class.java, *args)
}
