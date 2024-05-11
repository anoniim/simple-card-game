tasks.register<Copy>("setExecutablePermission") {
    description = "Sets executable permission for the generated .app bundle on macOS"
    doLast {
        val osName = System.getProperty("os.name")
        if (osName.startsWith("Mac OS")) {
            val appBundle = file("${layout.buildDirectory}/compose/binaries/main/app/simple-card-game.app")
            if (appBundle.exists()) {
                val executableFile = file("${appBundle}/Contents/MacOS/simple-card-game")
                executableFile.setExecutable(true, false)
                println("Executable permission set for: $executableFile")
            } else {
                println("Warning: .app bundle not found. Skipping setExecutablePermission task.")
            }
        } else {
            println("Not running on macOS. Skipping setExecutablePermission task.")
        }
    }
    dependsOn("createDistributable")
}

tasks.named("package") {
    dependsOn("setExecutablePermission")
}