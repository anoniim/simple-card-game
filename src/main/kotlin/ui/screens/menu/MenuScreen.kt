package ui.screens.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.AppLocale
import ui.Strings
import java.util.*

@Composable
fun MenuScreen(
    openNewGameMenu: () -> Unit,
    openLeaderboard: () -> Unit,
    onLocaleChange: (Locale) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource("img/menu.png"),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        MenuBox(
            openNewGameMenu,
            openLeaderboard,
            onLocaleChange,
            Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun MenuBox(
    openNewGameMenu: () -> Unit,
    openLeaderboard: () -> Unit,
    onLocaleChange: (Locale) -> Unit,
    alignmentModifier: Modifier
) {
    Column(
        modifier = alignmentModifier
            .fillMaxWidth(fraction = 0.4f)
            .clip(RoundedCornerShape(16.dp))
            .shadow(16.dp)
            .background(Color.Gray.copy(alpha = 0.9f))
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { openNewGameMenu() },
            modifier = Modifier.width(220.dp),
        ) {
            Text(
                text = Strings["new_game", AppLocale.current].uppercase()
            )
        }
        OutlinedButton(
            onClick = { openLeaderboard() },
            modifier = Modifier.padding(top = 16.dp)
                .width(220.dp),
        ) {
            Text(
                text = Strings["leaderboard", AppLocale.current].uppercase()
            )
        }
        LanguageBox(onLocaleChange)
    }
}

@Composable
fun LanguageBox(onLocaleChange: (Locale) -> Unit) {
    val locale = AppLocale.current
    Row(
        modifier = Modifier.width(220.dp)
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (locale == Locale("cs")) {
            LanguageButton(
                onClick = { onLocaleChange(java.util.Locale.ENGLISH) },
                text = "🇬🇧",
            )
            SelectedLanguageButton(
                text = "🇨🇿",
            )
        } else {
            SelectedLanguageButton(
                text = "🇬🇧",
            )
            LanguageButton(
                onClick = { onLocaleChange(Locale("cs")) },
                text = "🇨🇿",
            )
        }
    }
}

@Composable
fun SelectedLanguageButton(
    text: String,
) {
    Button(
        onClick = { /* clicked language already selected */ },
    ) {
        FlagIcon(text)
    }
}

@Composable
fun LanguageButton(
    onClick: () -> Unit,
    text: String,
) {
    OutlinedButton(
        onClick = onClick,
    ) {
        FlagIcon(text)
    }
}

@Composable
private fun FlagIcon(text: String) {
    Text(
        text,
        fontSize = 24.sp
    )
}
