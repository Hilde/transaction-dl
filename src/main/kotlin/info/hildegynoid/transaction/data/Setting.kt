package info.hildegynoid.transaction.data

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class Setting {
    private val yaml: Yaml
        get() {
            val options = DumperOptions()
            options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
            options.indicatorIndent = 2
            options.indent = 4
            return Yaml(options)
        }

    fun open(path: Path): SettingProperty {
        val file = path.toFile()
        if (!file.canRead()) {
            throw IOException("Cannot read setting file: $path")
        }

        val stream = FileInputStream(file)
        return yaml.loadAs(stream, SettingProperty::class.java)
    }

    fun save(path: Path, property: SettingProperty) {
        Files.write(path, yaml.dumpAsMap(property).toByteArray())
    }
}
