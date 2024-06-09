import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.koin.core.context.startKoin
import ui.AppComposable

fun main() = application {
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
