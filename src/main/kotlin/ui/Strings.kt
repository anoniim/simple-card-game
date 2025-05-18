package ui

import androidx.compose.runtime.staticCompositionLocalOf
import java.util.*

val AppLocale = staticCompositionLocalOf<Locale> { Locale.getDefault() }

object Strings {
    operator fun get(key: String, locale: Locale): String {
        val bundle = ResourceBundle.getBundle("Strings", locale)
        return bundle.getString(key)
    }
}