package engine

class CardDeck(
    numOfDecks: Int
) {
    private val deck = generateDeck(numOfDecks).shuffled().toMutableList()

    private fun generateDeck(numOfDecks: Int): List<Card> {
        return List(numOfDecks) { Card.entries }.flatten()
    }

    fun drawCard(): Card {
        return deck.removeAt(0)
    }
}

enum class Card(
    val displayValue: String,
    val points: Int,
) {
    CARD_1("1", 1),
    CARD_2("2", 2),
    CARD_3("3", 3),
    CARD_4("4", 4),
    CARD_5("5", 5),
    CARD_6("6", 6),
    CARD_7("7", 7),
    CARD_8("8", 8),
    CARD_9("9", 9),
    CARD_10("10", 10),
    CARD_J("J", 11),
    CARD_Q("Q", 12),
    CARD_K("K", 13),
}