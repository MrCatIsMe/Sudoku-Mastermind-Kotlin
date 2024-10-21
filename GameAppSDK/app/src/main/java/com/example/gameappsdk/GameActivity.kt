package com.example.gameappsdk

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Date
import java.util.Locale
import java.util.Random
import kotlin.math.min
import kotlin.text.*

class GameActivity : AppCompatActivity() {
    private var selectedCell: TextView? = null // To store the selected cell
    private lateinit var timerTextView: TextView // Timer display
    private lateinit var countDownTimer: CountDownTimer // Countdown timer
    private var timeLeftInMillis: Long = 600000 // Set to 5 minutes (300000 milliseconds)
    private lateinit var solutionBoard: Array<IntArray> // Lưu lời giải hoàn chỉnh

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        timerTextView = findViewById(R.id.timerTextView) // Initialize timer TextView
        startTimer() // Start the countdown timer

        val difficulty = intent.getStringExtra("DIFFICULTY_LEVEL")

        // Handle game mode based on difficulty
        when (difficulty) {
            "Easy" -> setupGame(40) // Example: 40 pre-filled cells for easy mode
            "Medium" -> setupGame(35)
            "Hard" -> setupGame(30)
            "Very Hard" -> setupGame(25)
            "Extreme" -> setupGame(20)
        }

        setupNumberButtons() // Call the method to set up number buttons
        setupCheckSolutionButton() // Call the method to set up check solution button
        setupDeleteButton() // Add this to set up the delete button
        setupTipButton()  // Add this to set up the TIP button
        setupResetButton() // Thiết lập nút "Bắt đầu lại"
    }

    private fun setupTipButton() {
        val tipButton = findViewById<Button>(R.id.buttonTip) // Find the "Gợi ý" button

        tipButton.setOnClickListener {
            selectedCell?.let { cell ->
                // Find the position of the selected cell
                val gridLayout = findViewById<GridLayout>(R.id.sudokuGrid)
                val row = gridLayout.indexOfChild(cell) / 9
                val col = gridLayout.indexOfChild(cell) % 9

                // Only provide hint if the cell is empty
                if (cell.text.isNullOrEmpty()) {
                    val correctSolution = getCorrectSolutionForCell(row, col)

                    if (correctSolution != 0) {
                        // Update the selected cell with the correct number as a hint
                        cell.text = correctSolution.toString()
                        cell.setTextColor(Color.BLACK) // Set hint cell text color to black
                    } else {
                        Toast.makeText(this, "Cannot provide hint for this cell", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Cannot provide hint for this cell", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun getCorrectSolutionForCell(row: Int, col: Int): Int {
        // Trả về giá trị đúng từ solutionBoard
        return solutionBoard[row][col]
    }

    private fun setupDeleteButton() {
        val deleteButton = findViewById<Button>(R.id.buttonX) // Find the delete button

        deleteButton.setOnClickListener {
            // Check if a cell is selected
            selectedCell?.let {
                it.text = "" // Clear the text in the selected cell
            }
        }
    }

    // Start the countdown timer
    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
                Toast.makeText(this@GameActivity, "Time's up!", Toast.LENGTH_SHORT).show()
                // Disable all cells when time is up
                disableAllCells()
            }
        }.start()
    }

    // Update the timer display
    @SuppressLint("DefaultLocale")
    private fun updateTimer() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        timerTextView.text = timeFormatted
    }

    // Disable all input cells in the Sudoku board when time is up
    private fun disableAllCells() {
        val gridLayout = findViewById<GridLayout>(R.id.sudokuGrid)
        for (i in 0 until gridLayout.childCount) {
            val cell = gridLayout.getChildAt(i) as TextView
            cell.isEnabled = false // Disable all cells
        }
    }

    // Method to set up the game with a specified number of filled cells
    private fun setupGame(filledCells: Int) {
        val sudokuBoard = Array(9) { IntArray(9) { 0 } }
        solutionBoard = Array(9) { IntArray(9) { 0 } } // Khởi tạo solutionBoard

        // Generate a valid Sudoku board
        generateSudoku(sudokuBoard)

        // Copy the complete solution to solutionBoard
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                solutionBoard[i][j] = sudokuBoard[i][j]
            }
        }

        // Remove random cells based on difficulty
        removeCells(sudokuBoard, filledCells)

        // Display Sudoku board on GridLayout
        displaySudokuBoard(sudokuBoard)
    }

    private fun isSafe(board: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        // Check row
        for (x in 0 until 9) {
            if (board[row][x] == num) {
                return false
            }
        }

        // Check column
        for (x in 0 until 9) {
            if (board[x][col] == num) {
                return false
            }
        }

        // Check 3x3 box
        val startRow = row - row % 3
        val startCol = col - col % 3
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                if (board[i + startRow][j + startCol] == num) {
                    return false
                }
            }
        }
        return true
    }

    private fun generateSudoku(board: Array<IntArray>): Boolean {
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                if (board[row][col] == 0) {
                    for (num in 1..9) {
                        if (isSafe(board, row, col, num)) {
                            board[row][col] = num

                            if (generateSudoku(board)) {
                                return true
                            }

                            board[row][col] = 0 // Backtrack
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun removeCells(board: Array<IntArray>, filledCells: Int) {
        val random = Random()
        var count = 81 - filledCells // Total cells are 81

        while (count > 0) {
            val row = random.nextInt(9)
            val col = random.nextInt(9)

            // Ensure the current cell is not already empty
            if (board[row][col] != 0) {
                board[row][col] = 0 // Clear number
                count--
            }
        }
    }

    private fun displaySudokuBoard(board: Array<IntArray>) {
        val gridLayout = findViewById<GridLayout>(R.id.sudokuGrid)
        gridLayout.removeAllViews()

        gridLayout.post {
            val gridWidth = gridLayout.width
            val gridHeight = gridLayout.height
            val cellSize = min(gridWidth, gridHeight) / 9

            for (i in 0 until 9) {
                for (j in 0 until 9) {
                    val cell = TextView(this)
                    cell.text = if (board[i][j] != 0) board[i][j].toString() else ""
                    cell.setBackgroundResource(R.drawable.cell_background) // Background for each cell
                    cell.gravity = Gravity.CENTER
                    cell.textSize = 18f

                    val params = GridLayout.LayoutParams()
                    params.width = cellSize
                    params.height = cellSize
                    params.columnSpec = GridLayout.spec(j)
                    params.rowSpec = GridLayout.spec(i)

                    // Check for the borders of the 3x3 boxes
                    val borderStyle = GradientDrawable()
                    borderStyle.setStroke(1, Color.BLACK) // Regular border
                    if (i % 3 == 0) borderStyle.setStroke(3, Color.BLACK) // Thicker top border for 3x3 block
                    if (j % 3 == 0) borderStyle.setStroke(3, Color.BLACK) // Thicker left border for 3x3 block
                    if (i == 8) borderStyle.setStroke(3, Color.BLACK) // Bottom border for the last row
                    if (j == 8) borderStyle.setStroke(3, Color.BLACK) // Right border for the last column

                    cell.background = borderStyle

                    // Set click listener for the cell
                    cell.setOnClickListener {
                        selectedCell?.setBackgroundResource(R.drawable.cell_background) // Deselect previous cell
                        selectedCell = cell
                        cell.setBackgroundColor(Color.YELLOW) // Highlight selected cell
                    }

                    gridLayout.addView(cell, params)
                }
            }
        }
    }

    private fun setupNumberButtons() {
        val numberButtons = listOf(
            findViewById<Button>(R.id.button1),
            findViewById<Button>(R.id.button2),
            findViewById<Button>(R.id.button3),
            findViewById<Button>(R.id.button4),
            findViewById<Button>(R.id.button5),
            findViewById<Button>(R.id.button6),
            findViewById<Button>(R.id.button7),
            findViewById<Button>(R.id.button8),
            findViewById<Button>(R.id.button9)
        )

        for (button in numberButtons) {
            button.setOnClickListener {
                val selectedNumber = button.text.toString()
                selectedCell?.let {
                    it.text = selectedNumber
                    it.setTextColor(Color.RED) // Set filled cell text color to red
                }
            }
        }
    }


    private fun setupCheckSolutionButton() {
        val checkSolutionButton = findViewById<Button>(R.id.checkSolutionButton)
        checkSolutionButton.setOnClickListener {
            // Logic để kiểm tra xem bảng hiện tại có khớp với lời giải hay không
            val gridLayout = findViewById<GridLayout>(R.id.sudokuGrid)
            var isCorrect = true

            for (i in 0 until 9) {
                for (j in 0 until 9) {
                    val cell = gridLayout.getChildAt(i * 9 + j) as TextView
                    val userValue = cell.text.toString().toIntOrNull() ?: 0

                    if (userValue != solutionBoard[i][j]) {
                        isCorrect = false
                        cell.setTextColor(Color.RED) // Tô đỏ những ô sai
                    } else {
                        cell.setTextColor(Color.BLACK) // Reset lại màu ô đúng
                    }
                }
            }

            if (isCorrect) {
                Toast.makeText(this, "Bạn đã giải thành công!", Toast.LENGTH_SHORT).show()

                // Ngưng bộ đếm thời gian
                countDownTimer.cancel()

                // Lấy thời gian còn lại (tính ra thời gian đã dùng)
                val timeTaken = 600000 - timeLeftInMillis

                // Lưu thành tích vào Firebase
                savePlayerRecordToFirebase(timeTaken)

            } else {
                Toast.makeText(this, "Có lỗi trong lời giải của bạn.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun savePlayerRecordToFirebase(timeTaken: Long) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val database = FirebaseDatabase.getInstance().reference

        // Chuyển đổi thời gian thành định dạng phút và giây
        val minutes = (timeTaken / 1000) / 60
        val seconds = (timeTaken / 1000) % 60

        // Tạo lịch sử kết quả
        val historyData = mapOf(
            "date" to getCurrentDate(),
            "difficulty" to intent.getStringExtra("DIFFICULTY_LEVEL"),
            "score" to calculateScore(timeTaken),
            "timeTaken" to String.format("%02d phút %02d giây", minutes, seconds)
        )

        // Lưu kết quả vào Firebase
        user?.let {
            val userId = it.uid
            val userEmail = it.email ?: "unknown@example.com" // Lấy email người dùng (username)

            // Lưu lịch sử vào nhánh "history" của người dùng
            database.child("users").child(userId).child("history").push().setValue(historyData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Lưu thành tích thành công!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Không thể lưu thành tích.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Hàm tính điểm (score có thể tùy chỉnh tùy theo logic của bạn)
    private fun calculateScore(timeTaken: Long): Int {
        // Ví dụ logic: thời gian càng ít điểm càng cao
        return (600000 - timeTaken).toInt() / 1000
    }

    // Hàm lấy ngày hiện tại
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }


    private fun resetGame() {
        val difficulty = intent.getStringExtra("DIFFICULTY_LEVEL")

        // Xử lý reset dựa trên mức độ khó hiện tại
        when (difficulty) {
            "Easy" -> setupGame(40) // Ví dụ: 40 ô được điền trước cho chế độ dễ
            "Medium" -> setupGame(35)
            "Hard" -> setupGame(30)
            "Very Hard" -> setupGame(25)
            "Extreme" -> setupGame(20)
        }

        // Đặt lại bộ đếm thời gian nếu cần
        countDownTimer.cancel() // Hủy timer hiện tại
        timeLeftInMillis = 600000 // Đặt lại thời gian (10 phút)
        startTimer() // Bắt đầu lại bộ đếm thời gian
    }
    private fun setupResetButton() {
        val resetButton = findViewById<Button>(R.id.resetButton) // Tìm nút "Bắt đầu lại"

        resetButton.setOnClickListener {
            resetGame() // Gọi phương thức resetGame để làm mới bảng
            Toast.makeText(this, "Trò chơi đã được làm mới!", Toast.LENGTH_SHORT).show()
        }
    }


}
