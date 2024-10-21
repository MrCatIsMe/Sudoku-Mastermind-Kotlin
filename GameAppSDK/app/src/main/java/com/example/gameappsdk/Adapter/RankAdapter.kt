package com.example.gameappsdk.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gameappsdk.R

data class PlayerRank(val username: String, val score: Int, val difficulty: String)

class RankAdapter(private val rankList: MutableList<PlayerRank>) : RecyclerView.Adapter<RankAdapter.RankViewHolder>() {

    inner class RankViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewUsername: TextView = view.findViewById(R.id.textViewUsername)
        val textViewScore: TextView = view.findViewById(R.id.textViewScore)
        val textViewDifficulty: TextView = view.findViewById(R.id.textViewDifficulty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player_rank, parent, false)
        return RankViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RankViewHolder, position: Int) {
        val rankItem = rankList[position]
        holder.textViewUsername.text = rankItem.username
        holder.textViewScore.text = "Score: ${rankItem.score}"
        holder.textViewDifficulty.text = "Difficulty: ${rankItem.difficulty}"
    }

    override fun getItemCount(): Int = rankList.size
}

data class RankItem(
    val username: String,
    val score: Int,
    val difficulty: String
)
