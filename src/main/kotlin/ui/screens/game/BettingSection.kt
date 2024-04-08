package ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import engine.player.Player
import getHighestBetInCoins

@Composable
internal fun BettingSection(
    players: List<Player>,
    onPlayerBet: (Int) -> Unit,
    onPlayerPass: () -> Unit
) {
    println(players)
    // Show betting section only if it's human player's turn
    val humanPlayer = players.find { it.isCurrentPlayer && it.isHuman }
    if (humanPlayer != null && humanPlayer.bet == null) {
        val highestBet = getHighestBetInCoins(players)
        val maxPossibleBet = humanPlayer.coins
        BetInputField(
            rememberBetInputStateHolder(minBet = highestBet + 1, maxPossibleBet),
            onBetConfirmed = {
                onPlayerBet(it)
            },
            playerPassed = {
                onPlayerPass()
            },
        )
    }
}

@Composable
internal fun BetInputField(
    state: BetInputStateHolder,
    onBetConfirmed: (Int) -> Unit,
    playerPassed: () -> Unit
) {
    Column(
        Modifier.width(200.dp)
            .offset(x = -20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.canBet) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = { state.decrease() }) {
                    IconText("-")
                }
                IconText(text = "$BEANS_SYMBOL ${state.betInput}")
                TextButton(onClick = { state.increase() }) {
                    IconText("+")
                }
            }
        }
        if (state.canBet) {
            Column {
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onBetConfirmed(state.bet)
                        state.resetBet()
                    }) {
                    Text(
                        text = "BET",
                        fontSize = 16.sp
                    )
                }
                Spacer(Modifier.width(16.dp))
                PassButton(state = state, playerPassed = playerPassed)
            }
        } else {
            PassButton(modifier = Modifier.padding(vertical = 32.dp), state, playerPassed)
        }
    }
}

@Composable
private fun PassButton(modifier: Modifier = Modifier, state: BetInputStateHolder, playerPassed: () -> Unit) {
    Button(
        onClick = {
            state.resetBet()
            playerPassed()
        }, modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "PASS",
            fontSize = 16.sp
        )
    }
}

@Composable
fun IconText(text: String) {
    // White text
    Text(
        text = text,
        fontSize = 30.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.White,
    )
}

@Composable
fun rememberBetInputStateHolder(minBet: Int, maxBet: Int) = remember {
    BetInputStateHolder(minBet, maxBet)
}

class BetInputStateHolder(
    private var minBet: Int,
    private var maxBet: Int
) {

    val canBet: Boolean
        get() = minBet <= maxBet

    var betInput by mutableStateOf("${if (minBet <= maxBet) minBet else ""}")
        private set

    var bet: Int = minBet
        private set


    fun updateBet(input: String) {
        if (input.isBlank()) {
            resetBet()
            return
        } else setIntInput(input)
    }

    private fun setIntInput(input: String) {
        try {
            bet = input.toInt()
            betInput = input
        } catch (e: NumberFormatException) {
            // Only numbers are allowed, ignore
        }
    }

    fun resetBet() {
        betInput = ""
        bet = 0
        maxBet = 0
    }

    fun increase() {
        if (bet < maxBet) {
            bet++
            betInput = bet.toString()
        }
    }

    fun decrease() {
        if (bet > minBet && bet > 0) {
            bet--
            betInput = bet.toString()
        }
    }
}