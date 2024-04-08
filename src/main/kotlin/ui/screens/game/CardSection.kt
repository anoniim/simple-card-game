package ui.screens.game

import GameEngine
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import engine.Card
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun CardSection(game: GameEngine, coroutineScope: CoroutineScope, alignmentModifier: Modifier) {
    val cardState = game.card.collectAsState()
    val firstCardDrawn = remember { mutableStateOf(false) }
    val cardSizeModifier = Modifier.width(194.dp).height(236.dp)
    val card = cardState.value
    if (!firstCardDrawn.value) {
        DrawFirstCardButton(
            alignmentModifier = alignmentModifier,
            cardSizeModifier = cardSizeModifier,
        ) {
            firstCardDrawn.value = true
            coroutineScope.launch { game.startGame() }
        }
    } else if (card != null) {
        CardView(alignmentModifier.then(cardSizeModifier), card)
    } else {
        Spacer(modifier = cardSizeModifier)
    }
}

@Composable
private fun DrawFirstCardButton(alignmentModifier: Modifier, cardSizeModifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = alignmentModifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            modifier = cardSizeModifier,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Draw first card!")
        }
    }
}

@Composable
fun CardView(modifier: Modifier, card: Card) {
    Card(
        modifier = modifier
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(
                text = card.displayValue,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .weight(1f),
                fontSize = 160.sp
            )
            Text(
                text = "Points: ${card.points}",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .padding(4.dp),
                fontSize = 20.sp
            )
        }
    }
}