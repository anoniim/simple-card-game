package ui.sound

import net.solvetheriddle.cardgame.SoundAction
import net.solvetheriddle.cardgame.SoundPlayer
import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

object JavaXSoundPlayer : SoundPlayer {
    override fun play(action: SoundAction) {
        playSound(action.soundFiles.random())
    }

    override fun loop(soundFileName: String) {
        try {
            val clip = createClip(soundFileName)
            clip.loop(Clip.LOOP_CONTINUOUSLY)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun playSound(soundFileName: String) {
        try {
            val clip = createClip(soundFileName)
            clip.start()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun createClip(soundFileName: String): Clip {
        val audioInputStream: AudioInputStream = AudioSystem.getAudioInputStream(File("src/main/resources/sounds/$soundFileName.wav"))
        return AudioSystem.getClip().apply {
            open(audioInputStream)
        }
    }
}