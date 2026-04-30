package com.anotasmart.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Venda : Screen("venda", "Venda", Icons.Default.ShoppingBag)
    object Produtos : Screen("produtos", "Produtos", Icons.Default.Inventory)
    object Pedidos : Screen("pedidos", "Pedidos", Icons.Default.ListAlt)
    object Clientes : Screen("clientes", "Clientes", Icons.Default.People)
    object Despesas : Screen("despesas", "Despesas", Icons.Default.AttachMoney)
    object Categorias : Screen("categorias", "Categorias", Icons.Default.Category)
    object Relatorios : Screen("relatorios", "Relatórios", Icons.Default.Assessment)
    object Documentacao : Screen("documentacao", "Documentação", Icons.Default.Description)
    object ChavePix : Screen("chave_pix", "Chave Pix", Icons.Default.QrCode)
    object Sobre : Screen("sobre", "Sobre", Icons.Default.Info)
}

val bottomNavItems = listOf(
    Screen.Venda,
    Screen.Produtos,
    Screen.Pedidos,
    Screen.Clientes,
    Screen.Despesas
)

val drawerNavItems = listOf(
    Screen.Categorias,
    Screen.Relatorios,
    Screen.Documentacao,
    Screen.ChavePix,
    Screen.Sobre
)
