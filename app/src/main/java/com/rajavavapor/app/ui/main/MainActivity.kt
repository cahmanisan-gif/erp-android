package com.rajavavapor.app.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView
import com.rajavavapor.app.R
import com.rajavavapor.app.data.SessionManager
import com.rajavavapor.app.databinding.ActivityMainBinding
import com.rajavavapor.app.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Tablet: NavigationRail, Phone: BottomNavigation
        val navRail = findViewById<NavigationRailView>(R.id.nav_rail)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        navRail?.setupWithNavController(navController)
            ?: bottomNav?.setupWithNavController(navController)
    }

    fun logout() {
        session.logout()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
