package ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// TODO change from Input field to up and down buttons (do not allow lower than minimum bet and higher than player's coins)

@Composable
internal fun BetInputField(
    state: BetInputStateHolder = rememberBetInputStateHolder(),
    onBetConfirmed: (Int) -> Unit,
    playerPassed: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Your bet: ")
        TextField(
            value = state.betInput,
            modifier = Modifier.height(48.dp)
                .width(64.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = { state.updateBet(it) })
        Spacer(Modifier.width(16.dp))
        Button(onClick = {
            state.resetBet()
            onBetConfirmed(state.bet)
        }
        ) {
            Text(text = "Confirm")
        }
        Spacer(Modifier.width(16.dp))
        Button(onClick = {
            state.resetBet()
            playerPassed()
        }) {
            Text(text = "Pass")
        }
    }
}

@Composable
fun rememberBetInputStateHolder() = remember { BetInputStateHolder() }

class BetInputStateHolder {
    var betInput by mutableStateOf("")
        private set

    var bet: Int = 0
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
    }
}