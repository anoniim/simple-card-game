package ui.screens.game

import GameEngine
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import engine.Card
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ui.AppLocale
import ui.Strings

@Composable
internal fun CardSection(game: GameEngine, firstCardDrawn: MutableState<Boolean>, coroutineScope: CoroutineScope, alignmentModifier: Modifier) {
    val cardState = game.card.collectAsState()
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
            Text(
                Strings["draw_first_card", AppLocale.current].uppercase(),
                fontSize = 20.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun CardView(alignmentModifier: Modifier, card: Card) {
    Image(
        painter = painterResource(card.image),
        contentDescription = null,
        modifier = alignmentModifier.fillMaxWidth(),
        contentScale = androidx.compose.ui.layout.ContentScale.Crop
    )
}