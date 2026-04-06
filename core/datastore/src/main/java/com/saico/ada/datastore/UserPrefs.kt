package com.saico.ada.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userDataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    private val USER_NAME = stringPreferencesKey("user_name")
    private val IS_MOTHER = booleanPreferencesKey("is_mother")
    private val OCCUPATION = stringPreferencesKey("occupation")

    val isOnboardingCompleted: Flow<Boolean> = context.userDataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }
    val userName: Flow<String?> = context.userDataStore.data.map { it[USER_NAME] }
    val isMother: Flow<Boolean> = context.userDataStore.data.map { it[IS_MOTHER] ?: false }

    suspend fun saveOnboardingCompleted(completed: Boolean) {
        context.userDataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }

    suspend fun saveUserData(name: String, isMother: Boolean, occupation: String) {
        context.userDataStore.edit {
            it[USER_NAME] = name
            it[IS_MOTHER] = isMother
            it[OCCUPATION] = occupation
            it[ONBOARDING_COMPLETED] = true
        }
    }
}
