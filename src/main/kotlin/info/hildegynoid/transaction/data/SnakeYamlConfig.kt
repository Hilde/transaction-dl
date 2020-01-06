package info.hildegynoid.transaction.data

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

@Configuration
class SnakeYamlConfig {
    @Bean
    fun yaml(): Yaml {
        val options = DumperOptions()
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        options.indicatorIndent = 2
        options.indent = 4
        return Yaml(options)
    }
}
