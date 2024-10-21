package com.example.gameappsdk

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.gameappsdk.Frame_Layout.HistoryFragment
import com.example.gameappsdk.Frame_Layout.HomeFragment
import com.example.gameappsdk.Frame_Layout.RankFragment
import com.example.gameappsdk.Frame_Layout.SettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        replaceFragment(HomeFragment())

        findViewById<BottomNavigationView>(R.id.bottom_nav_bar).setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.home -> {
                    replaceFragment(HomeFragment())
                }
                R.id.history -> {
                    replaceFragment(HistoryFragment())
                }
                R.id.setting -> {
                    replaceFragment(SettingFragment())
                }
                R.id.rank -> {
                    replaceFragment(RankFragment())
                }
            }
            return@setOnItemSelectedListener true

        }
    }
    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}