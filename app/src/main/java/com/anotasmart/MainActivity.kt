package com.anotasmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anotasmart.ui.navigation.Screen
import com.anotasmart.ui.navigation.bottomNavItems
import com.anotasmart.ui.screens.*
import com.anotasmart.ui.theme.AnotaSmartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnotaSmartTheme {
                val navController = rememberNavController()
                ScreenStructure(navController)
            }
        }
    }
}

@Composable
fun ScreenStructure(navController: NavHostController) {
    var quantidadeItens by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BarraSuperiorAnotaSmart(quantidadeItens = quantidadeItens)
        },
        bottomBar = {
            Column {
                if (quantidadeItens > 0) {
                    ResumoCarrinho(quantidadeItens = quantidadeItens)
                }
                BarraNavegacaoPrincipal(navController)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Venda.route
            ) {
                composable(Screen.Venda.route) { VendaScreen() }
                composable(Screen.Produtos.route) { ProdutosScreen() }
                composable(Screen.Pedidos.route) { PedidosScreen() }
                composable(Screen.Clientes.route) { ClientesScreen() }
                composable(Screen.Despesas.route) { DespesasScreen() }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperiorAnotaSmart(quantidadeItens: Int) {
    CenterAlignedTopAppBar(
        title = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil do Usuário"
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Navigation drawer"
                )
            }
        },
        actions = {
            IconButton(onClick = { }) {
                BadgedBox(
                    badge = {
                        if (quantidadeItens > 0) {
                            Badge {
                                Text(text = quantidadeItens.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Carrinho de Compras"
                    )
                }
            }
        }
    )
}

@Composable
fun ResumoCarrinho(quantidadeItens: Int) {
    Surface(
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TOTAL: R$ 51,00",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Quantidade: $quantidadeItens",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Button(onClick = { }) {
                Text("VER CARRINHO")
            }
        }
    }
}

@Composable
fun BarraNavegacaoPrincipal(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                alwaysShowLabel = false
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview(){
    AnotaSmartTheme {
        BarraSuperiorAnotaSmart(0)
    }
}
