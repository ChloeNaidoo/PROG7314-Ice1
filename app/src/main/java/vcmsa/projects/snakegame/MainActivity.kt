package vcmsa.projects.snakegame

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.snakeice2025.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnUp.setOnClickListener { binding.gameView.setDirection(Direction.UP) }
        binding.btnDown.setOnClickListener { binding.gameView.setDirection(Direction.DOWN) }
        binding.btnLeft.setOnClickListener { binding.gameView.setDirection(Direction.LEFT) }
        binding.btnRight.setOnClickListener { binding.gameView.setDirection(Direction.RIGHT) }

        binding.btnLeaderboard.setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }
    }
}
