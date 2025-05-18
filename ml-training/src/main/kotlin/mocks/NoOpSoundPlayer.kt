package mocks

import net.solvetheriddle.cardgame.SoundAction
import net.solvetheriddle.cardgame.SoundPlayer

class NoOpSoundPlayer: SoundPlayer {
    override fun play(action: SoundAction) {
        // No-op
    }

    override fun loop(soundFileName: String) {
        // No-op
    }
}
