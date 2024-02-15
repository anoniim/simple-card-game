package engine

@JvmInline
value class PlayerId(val value: Int)
class Player(
    val id: PlayerId,
    val name: String,
    var coins: Int,
    var score: Int,
)