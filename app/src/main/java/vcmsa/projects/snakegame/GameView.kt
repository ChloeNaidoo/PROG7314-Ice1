package vcmsa.projects.snakegame

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

enum class Direction { UP, DOWN, LEFT, RIGHT }

class GameView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint()
    private var snake = mutableListOf(Point(10, 10))
    private var direction = Direction.RIGHT
    private var food = Point(5, 5)
    private var gameOver = false
    private var score = 0
    private val cellSize = 40
    private val gestureDetector = GestureDetector(context, GestureListener())

    init {
        spawnFood()
        gameLoop()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawSnake(canvas)
        drawFood(canvas)
        if (gameOver) drawGameOver(canvas)
    }

    private fun gameLoop() {
        postDelayed(object : Runnable {
            override fun run() {
                if (!gameOver) {
                    move()
                    invalidate()
                    postDelayed(this, 300)
                }
            }
        }, 300)
    }

    fun setDirection(dir: Direction) {
        if ((dir == Direction.UP && direction != Direction.DOWN) ||
            (dir == Direction.DOWN && direction != Direction.UP) ||
            (dir == Direction.LEFT && direction != Direction.RIGHT) ||
            (dir == Direction.RIGHT && direction != Direction.LEFT)) {
            direction = dir
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent, e2: MotionEvent, vx: Float, vy: Float): Boolean {
            val dx = e2.x - e1.x
            val dy = e2.y - e1.y
            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 0) setDirection(Direction.RIGHT) else setDirection(Direction.LEFT)
            } else {
                if (dy > 0) setDirection(Direction.DOWN) else setDirection(Direction.UP)
            }
            return true
        }
    }

    private fun move() {
        val head = snake.first()
        val newHead = when (direction) {
            Direction.UP -> Point(head.x, head.y - 1)
            Direction.DOWN -> Point(head.x, head.y + 1)
            Direction.LEFT -> Point(head.x - 1, head.y)
            Direction.RIGHT -> Point(head.x + 1, head.y)
        }

        if (newHead in snake || newHead.x < 0 || newHead.y < 0 || newHead.x > width / cellSize || newHead.y > height / cellSize) {
            gameOver = true
            saveScore()
            return
        }

        snake.add(0, newHead)
        if (newHead == food) {
            score++
            spawnFood()
        } else {
            snake.removeLast()
        }
    }

    private fun spawnFood() {
        val maxX = width / cellSize
        val maxY = height / cellSize
        food = Point(Random.nextInt(maxX), Random.nextInt(maxY))
    }

    private fun drawSnake(canvas: Canvas) {
        paint.color = Color.GREEN
        for (part in snake) {
            canvas.drawRect(
                (part.x * cellSize).toFloat(),
                (part.y * cellSize).toFloat(),
                ((part.x + 1) * cellSize).toFloat(),
                ((part.y + 1) * cellSize).toFloat(),
                paint
            )
        }
    }

    private fun drawFood(canvas: Canvas) {
        paint.color = Color.RED
        canvas.drawRect(
            (food.x * cellSize).toFloat(),
            (food.y * cellSize).toFloat(),
            ((food.x + 1) * cellSize).toFloat(),
            ((food.y + 1) * cellSize).toFloat(),
            paint
        )
    }

    private fun drawGameOver(canvas: Canvas) {
        paint.color = Color.WHITE
        paint.textSize = 60f
        canvas.drawText("Game Over!", 100f, 300f, paint)
        canvas.drawText("Score: $score", 100f, 400f, paint)
    }

    private fun saveScore() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = Firebase.firestore
        val scoreMap = hashMapOf(
            "username" to (user.email ?: "unknown"),
            "score" to score
        )
        db.collection("scores").add(scoreMap)
    }
}
