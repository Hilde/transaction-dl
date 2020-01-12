package info.hildegynoid.transaction.data

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class SessionStorage {

    var sessionFile: Path = Paths.get("session.yaml")

    var property: UsersProperty = UsersProperty()

    private val yaml: Yaml
        get() {
            val options = DumperOptions()
            options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
            options.indicatorIndent = 2
            options.indent = 4
            return Yaml(options)
        }

    fun open(sessionFile: Path? = null) {
        if (sessionFile != null) {
            this.sessionFile = sessionFile
        }
        if (!Files.exists(this.sessionFile)) {
            throw FileNotFoundException("Session file not found: $sessionFile")
        }
        val stream = FileInputStream(this.sessionFile.toFile())
        property = yaml.loadAs(stream, UsersProperty::class.java)
    }

    fun save() {
        Files.write(sessionFile, yaml.dumpAsMap(property).toByteArray())
    }
}
