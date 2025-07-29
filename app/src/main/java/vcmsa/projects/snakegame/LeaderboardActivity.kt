package vcmsa.projects.snakegame

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.snakeice2025.databinding.ActivityLeaderboardBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LeaderboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLeaderboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        db.collection("scores")
            .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                val scores = result.map {
                    "${it.getString("username")}: ${it.getLong("score") ?: 0}"
                }
                binding.leaderboardList.adapter =
                    android.widget.ArrayAdapter(this, android.R.layout.simple_list_item_1, scores)
            }
    }
}
