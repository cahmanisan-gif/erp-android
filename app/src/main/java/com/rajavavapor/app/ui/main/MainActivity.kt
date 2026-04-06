package com.rajavavapor.app.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView
import com.rajavavapor.app.R
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.SessionManager
import com.rajavavapor.app.databinding.ActivityMainBinding
import com.rajavavapor.app.ui.login.LoginActivity
import com.rajavavapor.app.util.NetworkMonitor
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

        // Init API client with auth interceptor (handles 401 → auto logout)
        ApiClient.init(applicationContext)

        // Init network monitor
        NetworkMonitor.init(applicationContext)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val navRail = findViewById<NavigationRailView>(R.id.nav_rail)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        navRail?.setupWithNavController(navController)
            ?: bottomNav?.setupWithNavController(navController)

        fetchNotificationBadge(bottomNav, navRail)

        // Handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.navHostFragment.updatePadding(top = bars.top)
            bottomNav?.updatePadding(bottom = bars.bottom)
            navRail?.updatePadding(top = bars.top, left = bars.left)
            insets
        }

        // Observe network state — show/hide offline banner
        val bannerOffline = findViewById<View>(R.id.bannerOffline)
        NetworkMonitor.isOnline.observe(this) { online ->
            if (!online) {
                bannerOffline?.isVisible = true
                bannerOffline?.startAnimation(
                    AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
                )
            } else {
                bannerOffline?.isVisible = false
            }
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
