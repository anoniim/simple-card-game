package engine.player


class Player(
    val id: PlayerId,
    val name: String,
    var coins: Int, // TODO Move to GameState
    var score: Int, // TODO Move to GameState
    // TODO add isHuman flag
)

@JvmInline
value class PlayerId(val value: Int)

