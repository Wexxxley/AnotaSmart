package com.anotasmart.data.preferences

data class UserPreferences(
    val userName: String = "Usuário",
    val companyName: String = "Minha Empresa",
    val pixKey: String = "",
    val profileImagePath: String? = null,
    val selectedTheme: Int = 0 // 0: Padrão, 1: Claro, 2: Escuro
)
