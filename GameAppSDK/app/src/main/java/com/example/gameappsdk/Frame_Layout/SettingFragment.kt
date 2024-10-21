package com.example.gameappsdk.Frame_Layout

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gameappsdk.R
import com.google.firebase.auth.FirebaseAuth

class SettingFragment : Fragment() {

    private lateinit var soundSwitch: Switch
    private lateinit var darkModeSwitch: Switch
    private lateinit var changeLanguageButton: Button
    private lateinit var logoutButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        soundSwitch = view.findViewById(R.id.switchSound)
        darkModeSwitch = view.findViewById(R.id.switchDarkMode)
        changeLanguageButton = view.findViewById(R.id.buttonChangeLanguage)
        logoutButton = view.findViewById(R.id.buttonLogout)

        sharedPreferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)

        setupListeners()
        loadSettings()

        return view
    }

    private fun setupListeners() {
        // Xử lý âm thanh
        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("sound", isChecked)
            editor.apply()
            Toast.makeText(requireContext(), "Âm thanh: ${if (isChecked) "Bật" else "Tắt"}", Toast.LENGTH_SHORT).show()
        }

        // Xử lý chế độ tối
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("dark_mode", isChecked)
            editor.apply()
            Toast.makeText(requireContext(), "Chế độ tối: ${if (isChecked) "Bật" else "Tắt"}", Toast.LENGTH_SHORT).show()
        }

        // Thay đổi ngôn ngữ
        changeLanguageButton.setOnClickListener {
            Toast.makeText(requireContext(), "Chức năng thay đổi ngôn ngữ", Toast.LENGTH_SHORT).show()
        }

        // Đăng xuất
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Bạn đã đăng xuất", Toast.LENGTH_SHORT).show()
            // Điều hướng về màn hình đăng nhập hoặc home
        }
    }

    private fun loadSettings() {
        soundSwitch.isChecked = sharedPreferences.getBoolean("sound", true)
        darkModeSwitch.isChecked = sharedPreferences.getBoolean("dark_mode", false)
    }
}
