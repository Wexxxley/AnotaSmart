package com.anotasmart.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Venda : Screen("venda", "Venda", Icons.Default.ShoppingBag)
    object Produtos : Screen("produtos", "Produtos", Icons.Default.Inventory)
    object Pedidos : Screen("pedidos", "Pedidos", Icons.Default.ListAlt)
    object Clientes : Screen("clientes", "Clientes", Icons.Default.People)
    object Despesas : Screen("despesas", "Despesas", Icons.Default.AttachMoney)
}

val bottomNavItems = listOf(
    Screen.Venda,
    Screen.Produtos,
    Screen.Pedidos,
    Screen.Clientes,
    Screen.Despesas
)
