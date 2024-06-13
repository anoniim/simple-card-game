plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "net.solvetheriddle.cardgame.training"
version = "1"

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "net.solvetheriddle.cardgame.MainKt"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":engine"))

    // Py4J to communicate with Python
    implementation(files("../venv/share/py4j/py4j0.10.9.7.jar"))

    // Test
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}