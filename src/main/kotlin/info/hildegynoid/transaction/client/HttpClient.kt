package info.hildegynoid.transaction.client

import java.nio.file.Path
import java.time.LocalDate

interface HttpClient {
    enum class FileType(val param: String) {
        CSV("csv"),
        XML("xml"),
        XLS("xls")
    }

    fun login(username: String, password: String)
    fun download(start: LocalDate, end: LocalDate, fileType: FileType, outputDir: Path)
}
