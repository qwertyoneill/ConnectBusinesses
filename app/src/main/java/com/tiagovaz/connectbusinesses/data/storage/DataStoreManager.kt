package com.tiagovaz.connectbusinesses.data.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val REFRESH_KEY = stringPreferencesKey("refresh_token")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
    }

    // Save token
    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    // Save refresh token
    suspend fun saveRefreshToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[REFRESH_KEY] = token
        }
    }
    val refreshToken: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[REFRESH_KEY]
    }

    // Save user name
    suspend fun saveUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME_KEY] = name
        }
    }

    // Token flow
    val token: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[TOKEN_KEY]
    }

    // User name flow
    val userName: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME_KEY]
    }

    // Clear token only
    suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }

    // Clear ALL data (logout)
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
    private val AUTH_METHOD_KEY = stringPreferencesKey("auth_method")

    suspend fun saveAuthMethod(method: String) {
        context.dataStore.edit { it[AUTH_METHOD_KEY] = method }
    }

    val authMethod: Flow<String?> = context.dataStore.data.map { it[AUTH_METHOD_KEY] }
    private val LAST_MATCH_SEEN = stringPreferencesKey("last_match_seen")

    suspend fun saveLastMatchSeen(timestamp: String) {
        context.dataStore.edit {
            it[LAST_MATCH_SEEN] = timestamp
        }
    }

    val lastMatchSeen: Flow<String?> =
        context.dataStore.data.map { it[LAST_MATCH_SEEN] }


}

