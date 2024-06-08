package engine

class CardDeck(
    private val numOfDecks: Int
) {
    private val deck = generateShuffledDeck(numOfDecks)

    private fun generateShuffledDeck(numOfDecks: Int) = generateDeck(numOfDecks).shuffled().toMutableList()

    private fun generateDeck(numOfDecks: Int): List<Card> {
        return List(numOfDecks) { Card.entries }.flatten()
    }

    fun drawCard(): Card {
        if (deck.isEmpty()) deck.addAll(generateShuffledDeck(numOfDecks))
        return deck.removeAt(0)
    }
}

enum class Card(
    val displayValue: String,
    val points: Int,
    val image: String,
) {
    CARD_1("A", 1, "img/A.png"),
    CARD_2("2", 2, "img/2.png"),
    CARD_3("3", 3, "img/3.png"),
    CARD_4("4", 4, "img/4.png"),
    CARD_5("5", 5, "img/5.png"),
    CARD_6("6", 6, "img/6.png"),
    CARD_7("7", 7, "img/7.png"),
    CARD_8("8", 8, "img/8.png"),
    CARD_9("9", 9, "img/9.png"),
    CARD_10("10", 10, "img/10.png"),
    CARD_J("J", 11, "img/J.png"),
    CARD_Q("Q", 12, "img/Q.png"),
    CARD_K("K", 13, "img/K.png"),
}