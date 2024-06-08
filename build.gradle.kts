import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.0"
    id("org.jetbrains.compose") version("1.6.1")
}

group = "net.solvetheriddle.cardgame"
version = "1.0-SNAPSHOT"

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi)
            packageName = "simple-card-game"
            packageVersion = "1.0.0"
//            icon("src/main/resources/icon.png") // Not supported by Compose yet
        }
    }
}

apply(from = "gradle/customTasks.gradle.kts")

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(project(":game"))

    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)

    val koinVersion = "3.5.3"
    implementation(platform("io.insert-koin:koin-bom:$koinVersion"))
    implementation("io.insert-koin:koin-core")

    // Test
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
