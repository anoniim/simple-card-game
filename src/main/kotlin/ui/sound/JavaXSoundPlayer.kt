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
        val file = File("src/main/resources/sounds/$soundFileName.wav")
        val audioInputStream: AudioInputStream = AudioSystem.getAudioInputStream(file)

        // Get audio format and convert to a standard format that works on all platforms
        val format = audioInputStream.format
        val convertedFormat = javax.sound.sampled.AudioFormat(
            javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED,
            44100.0f, // Standard sample rate
            16, // Sample size in bits
            format.channels,
            format.channels * 2, // Frame size (channels * sample size in bytes)
            44100.0f,
            false // Little endian (works better on Windows)
        )

        // Convert the stream before getting the clip
        val convertedStream = AudioSystem.getAudioInputStream(convertedFormat, audioInputStream)

        // Now get the clip with a format that should be supported
        val clip = AudioSystem.getClip()
        clip.open(convertedStream)

        return clip
    }
}
