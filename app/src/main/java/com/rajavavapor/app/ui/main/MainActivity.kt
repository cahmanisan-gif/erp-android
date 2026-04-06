package com.rajavavapor.app.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView
import com.rajavavapor.app.R
import com.rajavavapor.app.data.SessionManager
import com.rajavavapor.app.databinding.ActivityMainBinding
import com.rajavavapor.app.ui.login.LoginActivity
import androidx.lifecycle.lifecycleScope
import com.rajavavapor.app.api.ApiClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
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

        // Fetch unread notification badge
        fetchNotificationBadge(bottomNav, navRail)

        // Handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Fragment container gets top padding (status bar)
            binding.navHostFragment.updatePadding(top = bars.top)
            // Bottom nav gets bottom padding (navigation bar)
            bottomNav?.updatePadding(bottom = bars.bottom)
            // Tablet rail gets top + left padding
            navRail?.updatePadding(top = bars.top, left = bars.left)
            insets
        }
    }

    private fun fetchNotificationBadge(
        bottomNav: BottomNavigationView?,
        navRail: NavigationRailView?
    ) {
        lifecycleScope.launch {
            try {
                val token = session.bearerToken()
                val response = ApiClient.service.getUnreadCount(token)
                if (response.success && response.count > 0) {
                    val badge = bottomNav?.getOrCreateBadge(R.id.navigation_notifikasi)
                        ?: navRail?.getOrCreateBadge(R.id.navigation_notifikasi)
                    badge?.apply {
                        isVisible = true
                        number = response.count
                        backgroundColor = getColor(R.color.brand_red)
                    }
                } else {
                    bottomNav?.removeBadge(R.id.navigation_notifikasi)
                    navRail?.removeBadge(R.id.navigation_notifikasi)
                }
            } catch (_: Exception) { }
        }
    }

    fun logout() {
        session.logout()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
