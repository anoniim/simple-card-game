package ui.screens.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import engine.GameDifficulty
import GamePrefs
import org.koin.java.KoinJavaComponent.get
import ui.AppLocale
import ui.Strings

@Composable
fun NewGameMenuScreen(playerName: MutableState<String>, startGame: () -> Unit, backToMainMenu: () -> Unit) {
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
            playerName, startGame, backToMainMenu,
            Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun MenuBox(
    playerName: MutableState<String>,
    startGame: () -> Unit,
    backToMainMenu: () -> Unit,
    alignmentModifier: Modifier
) {
    val prefs = remember { get<GamePrefs>(GamePrefs::class.java) }
    val difficulty = remember { mutableStateOf(prefs.loadGameDifficulty()) }

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
        OutlinedTextField(
            value = playerName.value,
            onValueChange = { playerName.value = it.uppercase().take(12) },
            label = { Text(Strings["player_name", AppLocale.current]) },
            modifier = Modifier.width(220.dp)
        )
        DifficultyBox(prefs, difficulty)
        Button(
            onClick = {
                startGame()
            },
            modifier = Modifier.padding(top = 16.dp)
                .width(220.dp)
        ) {
            Text(Strings["start_game", AppLocale.current].uppercase())
        }
        OutlinedButton(
            onClick = { backToMainMenu() },
            modifier = Modifier.padding(top = 16.dp)
                .width(220.dp)
        ) {
            Text(Strings["back", AppLocale.current].uppercase())
        }
    }
}

/** A row with 3 UI elements for selecting the difficulty of the game. Arrow left and right allows for cycling between difficulty levels.
 * The text in the middle between the arrows shows EASY/MEDIUM/HARD as the user clicks the arrows. */
@Composable
fun DifficultyBox(prefs: GamePrefs, difficulty: MutableState<GameDifficulty>) {
    Row(
        modifier = Modifier.padding(top = 24.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArrowButton(prefs, difficulty, "←", -1)
        DifficultyText(difficulty)
        ArrowButton(prefs, difficulty, "→", 1)
    }
}

@Composable
private fun ArrowButton(prefs: GamePrefs, difficulty: MutableState<GameDifficulty>, buttonText: String, increment: Int) {
    Button(
        onClick = {
            difficulty.update(increment)
            prefs.saveGameDifficulty(difficulty.value)
        },
        modifier = Modifier.height(36.dp),
    ) {
        ArrowText(buttonText)
    }
}

@Composable
private fun ArrowText(text: String) {
    Text(
        text,
        textAlign = TextAlign.Center,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
    )
}

@Composable
private fun DifficultyText(difficulty: MutableState<GameDifficulty>) {
    val difficultyText = when (difficulty.value) {
        GameDifficulty.EASY -> Strings["easy", AppLocale.current].uppercase()
        GameDifficulty.MEDIUM -> Strings["medium", AppLocale.current].uppercase()
        GameDifficulty.HARD -> Strings["hard", AppLocale.current].uppercase()
    }
    Text(
        difficultyText,
        modifier = Modifier.width(80.dp),
        textAlign = TextAlign.Center,
        color = Color.White,
        fontSize = 14.sp,
    )
}

private fun MutableState<GameDifficulty>.update(increment: Int) {
    val difficultySize = GameDifficulty.entries.size
    val newOrdinal = (value.ordinal + increment + difficultySize) % difficultySize
    value = GameDifficulty.entries[newOrdinal]
}
