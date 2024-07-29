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
    val options = FirebaseOptions.Builder()
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
