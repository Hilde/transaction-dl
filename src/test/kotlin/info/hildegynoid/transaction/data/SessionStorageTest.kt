package info.hildegynoid.transaction.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.FileReader
import java.nio.file.Path

@ExtendWith(SpringExtension::class)
@Configuration
@Import(SessionStorage::class, SnakeYamlConfig::class)
internal class SessionStorageTest {
    @Autowired
    lateinit var storage: SessionStorage

    @Test
    fun read() {
        storage.open(ClassPathResource("unit-test.yaml").file.toPath())

        assertAll(
            { assertEquals("Jon Doe", storage.property.users[0].username) },
            { assertEquals("1234abcd", storage.property.users[0].sessionToken) },
            { assertEquals("Jane Doe", storage.property.users[1].username) },
            { assertEquals("9876dcba", storage.property.users[1].sessionToken) }
        )
    }

    @Test
    fun save(@TempDir tempDir: Path) {
        val property = UsersProperty(
            listOf(UsersProperty.User("Foo Bar", "123"), UsersProperty.User("Baz"))
        )

        val file = tempDir.resolve("test.yaml")
        storage.sessionFile = file
        storage.property = property
        storage.save()

        val content = FileReader(file.toFile()).readText()
        assertEquals(
            """users:
            |  - sessionToken: '123'
            |    username: Foo Bar
            |  - sessionToken: ''
            |    username: Baz
            |
        """.trimMargin(), content
        )
    }
}
