package ui.screens.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import engine.AiPlayerDifficulty
import engine.GamePrefs
import engine.GameSettings
import org.koin.java.KoinJavaComponent.get

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
    val difficulty = remember { mutableStateOf(AiPlayerDifficulty.MEDIUM) }

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
            label = { Text("Enter your name") },
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        DifficultyBox(prefs, difficulty)
        Button(
            onClick = {
                startGame()
            },
            modifier = Modifier.padding(top = 16.dp)
                .width(220.dp)
        ) {
            Text("START GAME")
        }
        OutlinedButton(
            onClick = { backToMainMenu() },
            modifier = Modifier.padding(top = 16.dp)
                .width(220.dp)
        ) {
            Text("BACK")
        }
    }
}

/** A row with 3 UI elements for selecting the difficulty of the game. Arrow left and right allows for cycling between difficulty levels.
 * The text in the middle between the arrows shows EASY/MEDIUM/HARD as the user clicks the arrows. */
@Composable
fun DifficultyBox(prefs: GamePrefs, difficulty: MutableState<AiPlayerDifficulty>) {
    Row(
        modifier = Modifier.padding(top = 16.dp)
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
private fun ArrowButton(prefs: GamePrefs, difficulty: MutableState<AiPlayerDifficulty>, buttonText: String, increment: Int) {
    Button(
        onClick = {
            difficulty.update(increment)
            val settings = when (difficulty.value) {
                AiPlayerDifficulty.EASY -> GameSettings.EASY
                AiPlayerDifficulty.MEDIUM -> GameSettings.DEFAULT
                AiPlayerDifficulty.HARD -> GameSettings.HARD
            }
            prefs.saveGameSettings(settings)
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
private fun DifficultyText(difficulty: MutableState<AiPlayerDifficulty>) {
    val difficultyText = when (difficulty.value) {
        AiPlayerDifficulty.EASY -> "EASY"
        AiPlayerDifficulty.MEDIUM -> "MEDIUM"
        AiPlayerDifficulty.HARD -> "HARD"
    }
    Text(
        difficultyText,
        modifier = Modifier.width(80.dp),
        textAlign = TextAlign.Center,
        color = Color.White,
    )
}

private fun MutableState<AiPlayerDifficulty>.update(increment: Int) {
    val difficultySize = AiPlayerDifficulty.entries.size
    val newOrdinal = (value.ordinal + increment + difficultySize) % difficultySize
    value = AiPlayerDifficulty.entries[newOrdinal]
}
