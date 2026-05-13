package com.anotasmart.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anotasmart.data.preferences.UserPreferences
import com.anotasmart.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserPreferencesRepository) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = repository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun updateUserName(name: String) {
        viewModelScope.launch {
            repository.updateUserName(name)
        }
    }

    fun updateCompanyName(name: String) {
        viewModelScope.launch {
            repository.updateCompanyName(name)
        }
    }

    fun updatePixKey(key: String) {
        viewModelScope.launch {
            repository.updatePixKey(key)
        }
    }

    fun updateProfileImage(path: String?) {
        viewModelScope.launch {
            repository.updateProfileImagePath(path)
        }
    }

    fun updateTheme(themeIndex: Int) {
        viewModelScope.launch {
            repository.updateTheme(themeIndex)
        }
    }
}
