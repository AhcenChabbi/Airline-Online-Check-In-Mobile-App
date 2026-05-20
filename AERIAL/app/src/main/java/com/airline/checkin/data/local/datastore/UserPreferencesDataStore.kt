package com.airline.checkin.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.userPrefsDataStore: DataStore<Preferences> by
        preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesDataStore
@Inject
constructor(@ApplicationContext private val context: Context) {
    private object Keys {
        val userId = stringPreferencesKey("user_id")
    }

    val userId: Flow<String?> =
            context.userPrefsDataStore.data
                    .catch { error ->
                        if (error is IOException) emit(emptyPreferences()) else throw error
                    }
                    .map { it[Keys.userId] }

    suspend fun setUserId(value: String?) {
        context.userPrefsDataStore.edit { prefs ->
            if (value.isNullOrBlank()) prefs.remove(Keys.userId) else prefs[Keys.userId] = value
        }
    }

    suspend fun clearUserId() {
        context.userPrefsDataStore.edit { it.remove(Keys.userId) }
    }
}
