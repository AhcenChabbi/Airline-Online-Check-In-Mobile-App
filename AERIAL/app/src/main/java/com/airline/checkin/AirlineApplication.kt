package com.airline.checkin

import android.app.Application
import com.airline.checkin.core.network.NetworkMonitor
import com.airline.checkin.core.security.TokenManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AirlineApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
        NetworkMonitor.init(this)
    }
}
