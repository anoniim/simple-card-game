plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "net.solvetheriddle.cardgame.training"
version = "1"

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "net.solvetheriddle.cardgame.TrainingEnvironmentKt"
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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}