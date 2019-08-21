package com.evans.insta

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.evans.insta.fragments.HomeFragment
import com.evans.insta.fragments.NotificationFragment
import com.evans.insta.fragments.ProfileFragment
import com.evans.insta.fragments.SearchFragment

class MainActivity : AppCompatActivity() {

    private var selectedFragment : Fragment? = null

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                selectedFragment = HomeFragment()
            }
            R.id.nav_search -> {
                selectedFragment = SearchFragment()
            }
            R.id.nav_add -> {
                return@OnNavigationItemSelectedListener true
            }R.id.nav_notifications -> {
                selectedFragment = NotificationFragment()
            }R.id.nav_profile -> {
                selectedFragment = ProfileFragment()
            }
        }
        if(selectedFragment != null){
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, selectedFragment!!)
                .commit()
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .addToBackStack(null)
            .commit()
    }
}
