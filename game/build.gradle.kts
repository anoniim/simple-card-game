plugins {
    kotlin("jvm")
}

group = "net.solvetheriddle.cardgame"
version = "1.0-SNAPSHOT"

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