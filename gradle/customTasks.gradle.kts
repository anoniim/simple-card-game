tasks.register<Copy>("setExecutablePermission") {
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