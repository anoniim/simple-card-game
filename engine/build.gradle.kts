plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.0"
}

group = "net.solvetheriddle.cardgame.engine"
version = project.properties["applicationVersion"] as String

repositories {
    mavenCentral()
}

dependencies {

    // Coroutines
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1") // for Android
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.1") //  for JVM
    api("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.1") //  for JVM

    // Serialization
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Test
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}