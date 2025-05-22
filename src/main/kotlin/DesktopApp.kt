import android.app.Application
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
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
        onCloseRequest = ::exitApplication,
        icon = appIcon
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

    // First try to load from resources (for packaged application)
    val resourceStream = Thread.currentThread().contextClassLoader.getResourceAsStream("local.properties")
    if (resourceStream != null) {
        localProperties.load(resourceStream)
        println("Loaded local.properties from resources")
        return localProperties
    }

    // If not found in resources, try to find the file in the filesystem
    val localPropertiesFile = findFile("local.properties")
    if (localPropertiesFile != null && localPropertiesFile.exists()) {
        localProperties.load(FileInputStream(localPropertiesFile))
        println("Loaded local.properties from file: ${localPropertiesFile.absolutePath}")
    } else {
        println("Warning: local.properties file not found")
        // Set default values for Firebase configuration to prevent crashes
        localProperties.setProperty("firebase.projectId", "bid-to-win")
        localProperties.setProperty("firebase.apiKey", "AIzaSyBQIRMs3D3QtewEtLutkVBVUb6Db0FjUt0")
        localProperties.setProperty("firebase.appId", "1:960684824400:web:ab5f8b5b5d9a4c1e17fe55")
        localProperties.setProperty("firebase.storageBucket", "bid-to-win.appspot.com")
        println("Using default Firebase configuration")
    }

    return localProperties
}

private val appIcon = BitmapPainter(useResource("img/K.png", ::loadImageBitmap)) // Load from resources
