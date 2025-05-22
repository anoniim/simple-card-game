val setExecutablePermissionTask = tasks.register<Copy>("setExecutablePermission") {
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
}

// Task to build Windows executable
val buildWindowsExecutable = tasks.register("buildWindowsExecutable") {
    description = "Builds a Windows executable (.exe) file"
    group = "build"

    // This task depends on the packageExe task which is provided by the Compose plugin
    dependsOn("packageExe")
    // Make sure local.properties is copied to resources
    dependsOn("copyLocalPropertiesToResources")

    doLast {
        println("Building Windows executable...")

        // Check for the standalone .exe file
        val exeFile = file("${layout.buildDirectory}/compose/binaries/main/exe/simple-card-game-${project.version}.exe")
        if (exeFile.exists()) {
            println("Windows executable (.exe) created successfully at: ${exeFile.absolutePath}")
            println("This is a standalone executable file that can be run directly on Windows.")
        } else {
            throw GradleException("Failed to create Windows executable. EXE file not found at: ${exeFile.absolutePath}")
        }

        // Also check for the MSI installer which might be useful for distribution
        val msiFile = file("${layout.buildDirectory}/compose/binaries/main/msi/simple-card-game-${project.version}.msi")
        if (msiFile.exists()) {
            println("Windows MSI installer package also available at: ${msiFile.absolutePath}")
            println("The MSI installer can be used for system-wide installation.")
        }
    }
}

afterEvaluate {
    tasks.named("createDistributable") {
        finalizedBy(setExecutablePermissionTask)
    }
}

gradle.taskGraph.whenReady {
    allTasks.forEach { task ->
        println(task.path)
    }
}
