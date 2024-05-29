package ui

import engine.player.*
import getHighestBetInCoins
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

class Sounds(
    private val soundPlayer: SoundPlayer
) {
    fun aiPlayerBet(bet: Bet) {
        val action = when (bet) {
            is Pass -> SoundAction.OPPONENT_PASS
            is CoinBet -> {
                if (bet.coins < 3) SoundAction.BET_LITTLE
                else SoundAction.BET_HIGH
            }
        }
        soundPlayer.play(action)
    }

    fun humanPlayerBet(bet: Bet, players: List<Player>) {
        idleTimerJob?.cancel()
        val action = when (bet) {
            is Pass -> {
                val highestBet = getHighestBetInCoins(players)
                val maxPossibleBet = players.getCurrentPlayer().coins
                if (maxPossibleBet < highestBet) SoundAction.PASS_NO_CHOICE
                else SoundAction.PASS
            }

            is CoinBet -> {
                if (bet.coins < 3) SoundAction.BET_LITTLE
                else SoundAction.BET_HIGH
            }
        }
        soundPlayer.play(action)
    }

    fun gameOver(overallWinner: Player) {
        soundPlayer.play(if (overallWinner.isHuman) SoundAction.GAME_WIN else SoundAction.GAME_LOSS)
    }

    fun drawCard() {
        soundPlayer.play(SoundAction.DRAW_CARD)
    }

    fun roundWinner(roundWinner: Player, winningPoints: Int) {
        val action = if (roundWinner.isHuman) {
            // win
            if (winningPoints >= 10) SoundAction.ROUND_WIN_BIG_CARD
            else SoundAction.ROUND_WIN
        } else {
            // loss
            if (winningPoints >= 10) SoundAction.ROUND_LOSS_BIG_CARD
            else SoundAction.ROUND_LOSS
        }
        soundPlayer.play(action)
    }

    private var idleTimerJob: Job? = null

    fun idling(currentPlayer: Player) {
        if (currentPlayer.isHuman) startTimer { soundPlayer.play(SoundAction.IDLE) }
    }

    private fun startTimer(function: () -> Unit) {
        idleTimerJob = CoroutineScope(Dispatchers.Swing).launch {
            delay(10_000)
            function()
        }
    }
}

interface SoundPlayer {
    fun play(action: SoundAction)
}

enum class SoundAction(
    vararg val soundFiles: String
) {
    DRAW_CARD("card1", "card2", "card3", "card4", "card5", "card6"),
    BET_LITTLE("bean"),
    BET_HIGH("beans"),
    OPPONENT_PASS("knock-knock"),
    PASS("nechci", "nischt", "no-no", "nope", "to-si-nech", "tohle-vynecham", "knock-knock"),
    PASS_NO_CHOICE("na-to-nemam", "to-ne", "knock-knock"),
    ROUND_WIN("dobry", "good-job", "jo", "jo2", "juchu", "jupi", "nice", "yes"),
    ROUND_WIN_BIG_CARD("cha-cha", "dobry", "jo3", "yes2", "yes3", "yes4"),
    ROUND_LOSS("meh", "meh2", "meh3", "nee3", "nee4", "no-no2", "safra"),
    ROUND_LOSS_BIG_CARD("ajaj", "nee", "nee2", "oh-no", "sakra", "sakra2", "sakra3", "to-snad-ne"),
    GAME_WIN("clap-jupi", "clap-jupi2", "im-the-winner", "vyhra"),
    GAME_LOSS("clap"),
    IDLE("delej", "ja-nevim", "pojdme", "pojdme2", "pojdme3", "sup-sup", "tak-pojd");
}

object JavaXSoundPlayer : SoundPlayer {
    override fun play(action: SoundAction) {
        playSound(action.soundFiles.random())
    }

    private fun playSound(soundFileName: String) {
        try {
            val audioInputStream: AudioInputStream = AudioSystem.getAudioInputStream(File("src/main/resources/sounds/$soundFileName.wav"))
            val clip: Clip = AudioSystem.getClip()
            clip.open(audioInputStream)
            clip.start()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}