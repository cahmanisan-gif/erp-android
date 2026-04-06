package com.rajavavapor.app.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Monitors network connectivity and exposes it as LiveData.
 * Usage in Activity:
 *   NetworkMonitor.observe(this) { isOnline -> updateUI(isOnline) }
 */
object NetworkMonitor {

    private val _isOnline = MutableLiveData(true)
    val isOnline: LiveData<Boolean> = _isOnline

    private var registered = false

    fun init(context: Context) {
        if (registered) return
        registered = true

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Check initial state
        val activeNetwork = cm.activeNetwork
        val caps = cm.getNetworkCapabilities(activeNetwork)
        _isOnline.postValue(caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true)

        // Listen for changes
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        cm.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isOnline.postValue(true)
            }

            override fun onLost(network: Network) {
                _isOnline.postValue(false)
            }
        })
    }

    fun isCurrentlyOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val caps = cm.getNetworkCapabilities(cm.activeNetwork)
        return caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
