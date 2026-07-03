package com.anotasmart.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Venda : Screen("venda", "Venda", Icons.Default.ShoppingBag)
    object Produtos : Screen("produtos", "Produtos", Icons.Default.Inventory)
    object Pedidos : Screen("pedidos", "Pedidos", Icons.Default.ListAlt)
    object Clientes : Screen("clientes", "Clientes", Icons.Default.People)
    object Despesas : Screen("despesas", "Despesas", Icons.Default.MoneyOff)
    object Categorias : Screen("categorias", "Categorias", Icons.Default.Category)
    object Relatorios : Screen("relatorios", "Relatórios", Icons.Default.Assessment)
    object Tutoriais : Screen("tutoriais", "Tutoriais", Icons.Default.VideoLibrary)
    object ChavePix : Screen("chave_pix", "Chave Pix", Icons.Default.Key)
    object Sobre : Screen("sobre", "Sobre", Icons.Default.Info)
    object Carrinho : Screen("carrinho", "Carrinho", Icons.Default.ShoppingCart)
    object FinalizarVenda : Screen("finalizar_venda", "Finalizar Venda", Icons.Default.AttachMoney)
    object VendaSucesso : Screen("venda_sucesso", "Venda Realizada", Icons.Default.CheckCircle)
    object Setup : Screen("setup", "Configuração Inicial", Icons.Default.Person)
    object Loading : Screen("loading", "Carregando", Icons.Default.Info)
    object ClienteDetalhes : Screen("cliente_detalhes/{clientId}", "Detalhes do Cliente", Icons.Default.People) {
        fun createRoute(clientId: String) = "cliente_detalhes/$clientId"
    }
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
    Screen.ChavePix,
    Screen.Tutoriais,
    Screen.Sobre
)