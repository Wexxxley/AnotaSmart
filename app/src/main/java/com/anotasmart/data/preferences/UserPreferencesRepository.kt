package com.anotasmart.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val COMPANY_NAME = stringPreferencesKey("company_name")
        val PIX_KEY = stringPreferencesKey("pix_key")
        val PROFILE_IMAGE_PATH = stringPreferencesKey("profile_image_path")
        val SELECTED_THEME = intPreferencesKey("selected_theme")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                userName = preferences[PreferencesKeys.USER_NAME] ?: "Usuário",
                companyName = preferences[PreferencesKeys.COMPANY_NAME] ?: "Minha Empresa",
                pixKey = preferences[PreferencesKeys.PIX_KEY] ?: "",
                profileImagePath = preferences[PreferencesKeys.PROFILE_IMAGE_PATH],
                selectedTheme = preferences[PreferencesKeys.SELECTED_THEME] ?: 0
            )
        }

    suspend fun updateUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    suspend fun updateCompanyName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.COMPANY_NAME] = name
        }
    }

    suspend fun updatePixKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PIX_KEY] = key
        }
    }

    suspend fun updateProfileImagePath(path: String?) {
        context.dataStore.edit { preferences ->
            if (path == null) {
                preferences.remove(PreferencesKeys.PROFILE_IMAGE_PATH)
            } else {
                preferences[PreferencesKeys.PROFILE_IMAGE_PATH] = path
            }
        }
    }

    suspend fun updateTheme(themeIndex: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_THEME] = themeIndex
        }
    }
}
