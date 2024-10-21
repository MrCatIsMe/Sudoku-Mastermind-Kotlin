package com.example.gameappsdk.Frame_Layout

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gameappsdk.Adapter.PlayerRank
import com.example.gameappsdk.Adapter.RankAdapter
import com.example.gameappsdk.R
import com.google.firebase.database.*

class RankFragment : Fragment() {

    private lateinit var rankAdapter: RankAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var playerRankList: MutableList<PlayerRank>
    private lateinit var spinnerDifficulty: Spinner

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rank, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewRank)
        recyclerView.layoutManager = LinearLayoutManager(context)
        playerRankList = mutableListOf()

        spinnerDifficulty = view.findViewById(R.id.spinnerDifficulty)

        rankAdapter = RankAdapter(playerRankList)
        recyclerView.adapter = rankAdapter

        spinnerDifficulty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedDifficulty = parent.getItemAtPosition(position).toString()
                Log.d("RankFragment", "Selected difficulty: $selectedDifficulty")
                loadPlayerRanks(selectedDifficulty)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        return view
    }

    private fun loadPlayerRanks(difficulty: String) {
        val database = FirebaseDatabase.getInstance().reference.child("users")

        database.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                playerRankList.clear()
                if (snapshot.exists()) {
                    Log.d("RankFragment", "Data exists, processing...")
                    for (userSnapshot in snapshot.children) {
                        val username = userSnapshot.child("username").getValue(String::class.java) ?: ""
                        val historySnapshot = userSnapshot.child("history")

                        var highestScore = 0
                        for (history in historySnapshot.children) {
                            val score = history.child("score").getValue(Int::class.java) ?: 0
                            val difficultyLevel = history.child("difficulty").getValue(String::class.java) ?: ""

                            if (difficultyLevel == difficulty && score > highestScore) {
                                highestScore = score
                            }
                        }

                        if (highestScore > 0) {
                            playerRankList.add(PlayerRank(username, highestScore, difficulty))
                        }
                    }

                    playerRankList.sortByDescending { it.score }
                    Log.d("RankFragment", "Player rank list size: ${playerRankList.size}")
                    rankAdapter.notifyDataSetChanged()
                } else {
                    Log.d("RankFragment", "No data found.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RankFragment", "Database error: ${error.message}")
            }
        })
    }
}
