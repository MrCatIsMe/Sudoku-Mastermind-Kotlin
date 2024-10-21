package com.example.gameappsdk.Frame_Layout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gameappsdk.Adapter.HistoryAdapter
import com.example.gameappsdk.Data.HistoryItem
import com.example.gameappsdk.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryFragment : Fragment() {

    private lateinit var historyAdapter: HistoryAdapter
    private val historyList = mutableListOf<HistoryItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        // Khởi tạo RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewHistory)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Khởi tạo adapter và gán vào RecyclerView
        historyAdapter = HistoryAdapter(historyList)
        recyclerView.adapter = historyAdapter

        // Tải dữ liệu lịch sử từ Firebase
        loadGameHistory()

        return view
    }

    private fun loadGameHistory() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val database = FirebaseDatabase.getInstance().reference

        user?.let {
            val userId = it.uid
            database.child("users").child(userId).child("history")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        historyList.clear() // Xóa danh sách trước khi thêm dữ liệu mới
                        for (historySnapshot in snapshot.children) {
                            val history = historySnapshot.getValue(HistoryItem::class.java)
                            history?.let { historyList.add(it) }
                        }
                        // Cập nhật danh sách vào adapter
                        historyAdapter.updateHistoryList(historyList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), "Không thể lấy lịch sử đấu.", Toast.LENGTH_SHORT).show()
                    }
                })
        } ?: run {
            Toast.makeText(requireContext(), "Người dùng chưa đăng nhập.", Toast.LENGTH_SHORT).show()
        }
    }
}
