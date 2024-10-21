package com.example.gameappsdk.Frame_Layout

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.gameappsdk.R
import com.example.gameappsdk.GameActivity

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Find the button and set up an onClickListener
        val startGameButton: Button = view.findViewById(R.id.startGameButton)
        startGameButton.setOnClickListener {
            showDifficultyDialog()
        }

        return view
    }

    private fun showDifficultyDialog() {
        val difficultyLevels = arrayOf("Easy", "Medium", "Hard", "Very Hard", "Extreme")
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Select Difficulty Level")
            .setItems(difficultyLevels) { dialog, which ->
                val selectedDifficulty = difficultyLevels[which]
                startGame(selectedDifficulty) // Bắt đầu game với chế độ đã chọn
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    // Phương thức bắt đầu game dựa theo chế độ khó
    private fun startGame(difficulty: String) {
        val intent = Intent(activity, GameActivity::class.java)
        intent.putExtra("DIFFICULTY_LEVEL", difficulty)
        startActivity(intent)
    }
}
