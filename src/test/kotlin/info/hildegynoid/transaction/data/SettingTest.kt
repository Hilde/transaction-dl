package info.hildegynoid.transaction.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class SettingTest {

    @Test
    fun open() {
        val uri = javaClass.classLoader.getResource("setting/test.yml")!!.toURI()
        val setting = Setting()
        val property = setting.open(Paths.get(uri))
        assertEquals(2, property.users.count())
        assertAll({
            property.users[0].apply {
                assertEquals("Jon Doe", username)
                assertFalse(premium)
                assertEquals("1234abcd", sessionToken)
            }
        })
        assertAll({
            property.users[1].apply {
                assertEquals("Jane Doe", username)
                assertTrue(premium)
                assertEquals("9876dcba", sessionToken)
            }
        })
        assertEquals("/tmp", property.downloadDir)
        assertAll({
            property.downloadType.apply {
                assertFalse(csv)
                assertTrue(xls)
                assertTrue(xml)
            }
        })
    }

    @Test
    fun save(@TempDir tempDir: Path) {
        val property = SettingProperty().apply {
            users.add(SettingProperty.User(username = "John Resident", premium = false, sessionToken = ""))
            downloadDir = ""
            downloadType.csv = true
            downloadType.xls = true
            downloadType.xml = true
        }
        val file = tempDir.resolve("test-output.yml")
        val setting = Setting()
        setting.save(file, property)

        val content = Files.readAllLines(file).joinToString("\n")
        assertEquals(
            """
            downloadDir: ''
            downloadType:
                csv: true
                xls: true
                xml: true
            users:
              - premium: false
                sessionToken: ''
                username: John Resident
            """.trimIndent(), content
        )
    }
}
