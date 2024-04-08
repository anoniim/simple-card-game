package ui.screens.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// TODO change from Input field to up and down buttons (do not allow lower than minimum bet and higher than player's coins)

@Composable
internal fun BetInputField(
    state: BetInputStateHolder,
    onBetConfirmed: (Int) -> Unit,
    playerPassed: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (state.canBet) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(onClick = { state.increase() }) {
                    IconText("⬆")
                }
                IconText(text = state.betInput)
                TextButton(onClick = { state.decrease() }) {
                    IconText("⬇")
                }
            }
        }
        if (state.canBet) {
            Column {
                Button(onClick = {
                    onBetConfirmed(state.bet)
                    state.resetBet()
                }) {
                    Text(
                        text = "BET",
                        fontSize = 12.sp
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
    Button(onClick = {
        state.resetBet()
        playerPassed()
    }, modifier = modifier) {
        Text(
            text = "PASS",
            fontSize = 12.sp
        )
    }
}

@Composable
fun IconText(text: String) {
    // White text
    Text(
        text = text,
        fontSize = 20.sp,
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