package com.anotasmart.data.preferences

data class UserPreferences(
    val userName: String = "",
    val companyName: String = "",
    val pixKey: String = "",
    val profileImagePath: String? = null,
    val selectedTheme: Int = 0, // 0: Padrão, 1: Claro, 2: Escuro
    val isLoaded: Boolean = false
)
