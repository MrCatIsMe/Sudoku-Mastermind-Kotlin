package com.example.gameappsdk.Data

data class User(
    val username: String,
    val history: Map<String, GameHistory>
)

data class GameHistory(
    val date: String,
    val difficulty: String,
    val score: Int,
    val timeTaken: String
)

// Hàm lấy điểm cao nhất theo độ khó
fun getHighestScoresByDifficulty(users: Map<String, User>): Map<String, Pair<String, Int>> {
    val highestScores = mutableMapOf<String, Pair<String, Int>>() // Độ khó -> (Tên người chơi, Điểm số)

    for (user in users) {
        for (history in user.value.history.values) {
            val difficulty = history.difficulty
            val score = history.score
            val username = user.value.username

            // Kiểm tra xem điểm cao hơn đã có chưa
            val currentHighest = highestScores[difficulty]
            if (currentHighest == null || score > currentHighest.second) {
                highestScores[difficulty] = Pair(username, score)
            }
        }
    }

    return highestScores
}
