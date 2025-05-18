import android.app.Application
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.google.firebase.Firebase
import com.google.firebase.FirebaseOptions
import com.google.firebase.FirebasePlatform
import com.google.firebase.initialize
import org.koin.core.context.startKoin
import ui.AppComposable
import java.io.File
import java.io.FileInputStream
import java.util.Properties


fun main() = application {
    initializeFirebase()
    startKoin {
        modules(appModule)
    }
    Window(
        title = "Simple card game",
        state = WindowState(width = 960.dp, height = 540.dp),
        onCloseRequest = ::exitApplication
    ) {
        AppComposable()
    }
}

fun initializeFirebase() {
    initializeFirebasePlatform()

    val localProperties = loadProperties()
    val options = FirebaseOptions.Builder()
        .setProjectId(localProperties.getProperty("firebase.projectId"))
        .setApiKey(localProperties.getProperty("firebase.apiKey"))
        .setApplicationId(localProperties.getProperty("firebase.appId"))
        .setStorageBucket(localProperties.getProperty("firebase.storageBucket"))
        .build()

    Firebase.initialize(Application(), options)
}

private fun initializeFirebasePlatform() {
    FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {
        val storage = mutableMapOf<String, String>()

        override fun clear(key: String) {
            storage.remove(key)
        }

        override fun log(msg: String) = println(msg)

        override fun retrieve(key: String): String? {
            return storage[key]
        }

        override fun store(key: String, value: String) {
            storage[key] = value
        }
    })
}

@Suppress("SENSELESS_COMPARISON") // Incorrect warning
fun findFile(fileName: String): File? {
    var currentDir = File(".").absoluteFile // Start from the current directory
    do {
        val file = File(currentDir, fileName)
        if (file.exists()) return file
        currentDir = currentDir.parentFile // Move up one directory
    } while (currentDir != null)
    return null
}

private fun loadProperties(): Properties {
    val localProperties = Properties()
    val localPropertiesFile = findFile("local.properties")
    if (localPropertiesFile != null && localPropertiesFile.exists()) {
        localProperties.load(FileInputStream(localPropertiesFile))
    } else {
        println("local.properties file not found")
    }
    return localProperties
}
