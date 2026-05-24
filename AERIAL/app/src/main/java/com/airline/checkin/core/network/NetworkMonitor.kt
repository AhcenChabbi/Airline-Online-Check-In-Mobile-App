package com.airline.checkin.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NetworkMonitor {
    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline

    private var callback: ConnectivityManager.NetworkCallback? = null

    fun init(context: Context) {
        if (callback != null) return

        val cm =
                context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as
                        ConnectivityManager

        updateState(cm)

        val request =
                NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build()

        val cb =
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        updateState(cm)
                    }

                    override fun onLost(network: Network) {
                        updateState(cm)
                    }

                    override fun onUnavailable() {
                        _isOnline.value = false
                    }
                }

        cm.registerNetworkCallback(request, cb)
        callback = cb
    }

    private fun updateState(cm: ConnectivityManager) {
        val network = cm.activeNetwork
        val caps = cm.getNetworkCapabilities(network)
        _isOnline.value = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
