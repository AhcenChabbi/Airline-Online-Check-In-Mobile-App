package com.airline.checkin.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecureStorage {
    private const val FILE_NAME = "secure_prefs"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        if (::prefs.isInitialized) return

        val masterKey =
                MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

        prefs =
                EncryptedSharedPreferences.create(
                        context,
                        FILE_NAME,
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
    }

    fun putString(key: String, value: String?) {
        val editor = prefs().edit()
        if (value == null) {
            editor.remove(key)
        } else {
            editor.putString(key, value)
        }
        editor.apply()
    }

    fun getString(key: String): String? = prefs().getString(key, null)

    fun clear() {
        prefs().edit().clear().apply()
    }

    private fun prefs(): SharedPreferences {
        check(::prefs.isInitialized) { "SecureStorage is not initialized" }
        return prefs
    }
}
