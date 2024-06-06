package ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.AppLocale
import ui.Strings

@Composable
fun ExitDialog(modifier: Modifier, onExit: () -> Unit, onCancel: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize(fraction = 0.5f)
            .shadow(32.dp, RoundedCornerShape(32.dp))
            .clip(RoundedCornerShape(32.dp))
            .background(Color.Gray.copy(alpha = 0.95f))
            .padding(32.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TitleText()
            Spacer(Modifier.height(16.dp))
            MessageText()
            Spacer(Modifier.height(32.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DialogButton("exit_dialog_cancel", onCancel)
                DialogButton("exit_dialog_exit", onExit)
            }
        }
    }
}

@Composable
private fun TitleText() {
    Text(
        text = Strings["exit_dialog_title", AppLocale.current].uppercase(),
        color = MaterialTheme.colorScheme.onSecondary,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun MessageText() {
    Text(
        text = Strings["exit_dialog", AppLocale.current],
        color = MaterialTheme.colorScheme.onSecondary,
        fontSize = 20.sp,
    )
}

@Composable
private fun DialogButton(stringKey: String, onExit: () -> Unit) {
    TextButton(
        onClick = onExit,
        modifier = Modifier.width(150.dp)
            .padding(vertical = 8.dp)
    ) {
        Text(
            Strings[stringKey, AppLocale.current].uppercase(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}