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
            initialValue = UserPreferences(isLoaded = false)
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

    fun saveInitialSetup(name: String, company: String, imagePath: String?, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.saveInitialSetup(name, company, imagePath)
            onComplete()
        }
    }
}

class UserViewModelFactory(private val repository: UserPreferencesRepository) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
