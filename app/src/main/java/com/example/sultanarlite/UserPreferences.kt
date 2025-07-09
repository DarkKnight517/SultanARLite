package com.example.sultanarlite

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val WELCOME_SHOWN_KEY = booleanPreferencesKey("welcome_shown")
    }

    val welcomeShown: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[WELCOME_SHOWN_KEY] ?: false }

    suspend fun setWelcomeShown() {
        context.dataStore.edit { prefs ->
            prefs[WELCOME_SHOWN_KEY] = true
        }
    }
}
