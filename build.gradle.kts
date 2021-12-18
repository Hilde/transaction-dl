import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.32"
    id("org.openjfx.javafxplugin") version "0.0.10"
    id("org.beryx.runtime") version "1.12.7"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("com.google.osdetector") version "1.6.2"
}

group = "info.hildegynoid"
version = "0.4.0"

repositories {
    mavenCentral()
}

application {
    mainClass.set("info.hildegynoid.transaction.MyApplication")
}

val platform = if (osdetector.os == "osx") { "mac" } else if (osdetector.os == "windows") { "win" } else { osdetector.os }

val title = "Transaction DL"

val kotlinLoggingVersion = "1.7.8"
val logbackVersion = "1.2.8"
val slf4jVersion = "1.7.30"
val okhttpVersion = "4.9.3"
val jsoupVersion = "1.14.3"
val snakeYamlVersion = "1.29"
val junitVersion = "5.8.2"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")
    implementation("org.jsoup:jsoup:$jsoupVersion")

    implementation("org.yaml:snakeyaml:$snakeYamlVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}

tasks.test {
    useJUnitPlatform()
}

javafx {
    version = "17.0.1"
    modules = listOf("javafx.controls", "javafx.fxml")
}

runtime {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    modules.set(listOf("java.naming", "jdk.crypto.ec", "javafx.base", "javafx.controls", "javafx.fxml" ))

    jpackage {
        outputDir = "installers"
        imageName = "Transaction DL"
        appVersion = "1.0.0"
        skipInstaller = true
        when (platform) {
            "mac" -> imageOptions = listOf("--icon", "$buildDir/resources/main/icons/appicon.icns")
            "win" -> imageOptions = listOf("--icon", "$buildDir/resources/main/icons/appicon.ico")
        }
    }
}

tasks.register<Zip>("zip") {
    dependsOn("jpackage")
    archiveFileName.set("TransactionDL-v${project.version}-${platform}.zip")
    when (platform) {
        "mac" -> {
            from("build/installers/Transaction DL.app")
            into("Transaction DL.app")
        }
        "win" -> {
            from("build/installers/Transaction DL")
            into("Transaction DL")
        }
    }
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    disabledRules.set(setOf("import-ordering"))
}
