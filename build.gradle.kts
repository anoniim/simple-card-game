import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.0"
    id("org.jetbrains.compose") version("1.6.1")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "net.solvetheriddle.cardgame"
version = project.properties["applicationVersion"] as String

compose.desktop {
    application {
        mainClass = "DesktopAppKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Exe)
            packageName = "simple-card-game"
            packageVersion = project.properties["applicationVersion"] as String
//            icon("src/main/resources/icon.png") // Not supported by Compose yet

            // Configure Windows-specific options
            windows {
                // Ensure the application has enough memory to run
                jvmArgs += listOf("-Xms256m", "-Xmx1g")

                // Include the JVM with the application
                includeAllModules = true

                // Configure menu shortcuts
                menu = true

                // Configure installer options
                upgradeUuid = "5d7b8dce-0d7a-4f32-9d5b-a3c0e8c8d5f1"
                dirChooser = true
                perUserInstall = true
            }

            // Configure JVM options for all platforms
            jvmArgs += listOf(
                "-Dfile.encoding=UTF-8",
                "-Djava.awt.headless=false"
            )

            // Ensure resources are properly included
            modules("java.sql")
            modules("java.naming")
            modules("jdk.unsupported")
        }
    }
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "DesktopAppKt"
    }
}

// Copy local.properties to resources directory
tasks.register<Copy>("copyLocalPropertiesToResources") {
    from("local.properties")
    into("src/main/resources")
    doFirst {
        mkdir("src/main/resources")
    }
}

// Make sure the resources are processed before compilation
tasks.named("processResources") {
    dependsOn("copyLocalPropertiesToResources")
}

apply(from = "gradle/customTasks.gradle.kts")

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(project(":engine"))

    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)

    // Koin
    val koinVersion = "3.5.3"
    implementation(platform("io.insert-koin:koin-bom:$koinVersion"))
    implementation("io.insert-koin:koin-core")

    // Firebase
    implementation("dev.gitlive:firebase-database:1.13.0")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    // Test
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
