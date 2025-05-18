import com.google.firebase.Firebase
import com.google.firebase.firestore.*
import engine.rating.Leaderboard
import engine.rating.PlayerStats

class LeaderboardRepository {

    private val db = Firebase.firestore

    fun load(currentLeaderboard: Leaderboard, callback: (Leaderboard) -> Unit) {
        db.collection("leaderboard")
            .get()
            .addOnSuccessListener { result -> callback(currentLeaderboard.mergeWith(result)) }
            .addOnFailureListener { exception -> println("Error getting documents: $exception") }
    }

    fun update(leaderboard: Leaderboard) {
        leaderboard.stats
            .filterKeys(::isHumanPlayers)
            .forEach { (player, stats) -> db.update(player, stats) }
    }

    private fun isHumanPlayers(name: String): Boolean {
        return name.none { it.isLowerCase() }
    }

    private fun Leaderboard.mergeWith(result: QuerySnapshot): Leaderboard {
        val updatedPlayerStats = stats.toMutableMap()
        for (document in result) {
            println("${document.id} => ${document.data}")
            updatedPlayerStats[document.id] = PlayerStats(document.rating, document.totalGames, document.totalWins)
        }
        return Leaderboard(updatedPlayerStats)
    }

    private val QueryDocumentSnapshot.rating: Double
        get() = (get("rating") as Number).toDouble()

    private val QueryDocumentSnapshot.totalGames: Int
        get() = (get("totalGames") as Number).toInt()

    private val QueryDocumentSnapshot.totalWins: Int
        get() = (get("totalWins") as Number).toInt()

    private fun FirebaseFirestore.update(player: String, stats: PlayerStats) =
        collection("leaderboard")
            .document(player)
            .set(stats)
            .addOnFailureListener { exception ->
                println("Error updating Firestore for $player (${stats}: $exception")
            }
}