package ui

import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

interface Sounds {
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
    IDLE("delej", "ja-nevim", "pojdme", "pojdme2", "pojdme3", "sup-sup", "tak-pojd",);
}

object JavaXSounds: Sounds {
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