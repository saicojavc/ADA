package com.saico.ada.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "sleep_prefs")

@Singleton
class SleepPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val LAST_SCREEN_OFF = longPreferencesKey("last_screen_off")
    private val LAST_USER_PRESENT = longPreferencesKey("last_user_present")

    val lastScreenOff: Flow<Long> = context.dataStore.data.map { it[LAST_SCREEN_OFF] ?: 0L }
    val lastUserPresent: Flow<Long> = context.dataStore.data.map { it[LAST_USER_PRESENT] ?: 0L }

    suspend fun saveScreenOff(timestamp: Long) {
        context.dataStore.edit { it[LAST_SCREEN_OFF] = timestamp }
    }

    suspend fun saveUserPresent(timestamp: Long) {
        context.dataStore.edit { it[LAST_USER_PRESENT] = timestamp }
    }
}
