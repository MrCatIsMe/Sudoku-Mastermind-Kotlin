package com.example.gameappsdk.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gameappsdk.Data.HistoryItem
import com.example.gameappsdk.R

class HistoryAdapter(private var historyList: List<HistoryItem>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.textViewDate)
        val difficultyTextView: TextView = itemView.findViewById(R.id.textViewDifficulty)
        val scoreTextView: TextView = itemView.findViewById(R.id.textViewScore)
        val timeTextView: TextView = itemView.findViewById(R.id.textViewTimeTaken)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyItem = historyList[position]
        holder.dateTextView.text = historyItem.date
        holder.difficultyTextView.text = historyItem.difficulty
        holder.scoreTextView.text = historyItem.score.toString()
        holder.timeTextView.text = historyItem.timeTaken
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    // Hàm để cập nhật danh sách lịch sử
    fun updateHistoryList(newHistoryList: List<HistoryItem>) {
        historyList = newHistoryList
        notifyDataSetChanged() // Thông báo cho RecyclerView biết rằng dữ liệu đã thay đổi
    }
}
