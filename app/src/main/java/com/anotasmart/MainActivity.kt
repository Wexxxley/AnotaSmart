package com.anotasmart
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import com.anotasmart.ui.navigation.drawerNavItems
import com.anotasmart.ui.screens.vendas.VendaScreen
import com.anotasmart.ui.screens.produtos.ProdutosScreen
import com.anotasmart.ui.screens.pedidos.PedidosScreen
import com.anotasmart.ui.screens.clientes.ClientesScreen
import com.anotasmart.ui.screens.despesas.DespesasScreen
import com.anotasmart.ui.screens.categorias.CategoriasScreen
import com.anotasmart.ui.screens.relatorios.RelatoriosScreen
import com.anotasmart.ui.screens.documentacao.DocumentacaoScreen
import com.anotasmart.ui.screens.chavepix.ChavePixScreen
import com.anotasmart.ui.screens.sobre.SobreScreen
import com.anotasmart.ui.screens.carrinho.CarrinhoScreen
import com.anotasmart.ui.theme.AppTheme
import com.anotasmart.ui.viewModels.CartViewModel
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var selectedTheme by remember { mutableIntStateOf(0) } // 0: Padrão, 1: Claro, 2: Escuro
            
            val isDarkTheme = when (selectedTheme) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }

            AppTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val cartViewModel: CartViewModel = viewModel()
                    ScreenStructure(
                        navController = navController,
                        cartViewModel = cartViewModel,
                        selectedTheme = selectedTheme,
                        onThemeChange = { selectedTheme = it }
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenStructure(
    navController: NavHostController,
    cartViewModel: CartViewModel,
    selectedTheme: Int,
    onThemeChange: (Int) -> Unit
) {
    val items by cartViewModel.items.collectAsState()
    val totalValor by cartViewModel.totalValor.collectAsState()
    val quantidadeItens = items.sumOf { it.quantidade }.toInt()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet (modifier = Modifier.width(300.dp)){
                DrawerContent(
                    userName = "Wesley",
                    companyName = "AnotaSmart",
                    currentRoute = currentRoute,
                    selectedTheme = selectedTheme,
                    onThemeChange = onThemeChange,
                    onItemClick = { screen ->
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                BarraSuperior(
                    quantidadeItens = quantidadeItens,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onCartClick = { 
                        if (currentRoute != Screen.Carrinho.route) {
                            navController.navigate(Screen.Carrinho.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            },
            bottomBar = {
                Column {
                    if (quantidadeItens > 0 && currentRoute != Screen.Carrinho.route) {
                        ResumoCarrinho(
                            quantidadeItens = quantidadeItens,
                            totalValor = totalValor,
                            onVerCarrinhoClick = { 
                                if (currentRoute != Screen.Carrinho.route) {
                                    navController.navigate(Screen.Carrinho.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                    BarraNavegacaoPrincipal(navController)
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Venda.route
                ) {
                    composable(Screen.Venda.route) { VendaScreen(cartViewModel = cartViewModel) }
                    composable(Screen.Produtos.route) { ProdutosScreen() }
                    composable(Screen.Pedidos.route) { PedidosScreen() }
                    composable(Screen.Clientes.route) { ClientesScreen() }
                    composable(Screen.Despesas.route) { DespesasScreen() }
                    composable(Screen.Categorias.route) { CategoriasScreen() }
                    composable(Screen.Relatorios.route) { RelatoriosScreen() }
                    composable(Screen.Documentacao.route) { DocumentacaoScreen() }
                    composable(Screen.ChavePix.route) { ChavePixScreen() }
                    composable(Screen.Sobre.route) { SobreScreen() }
                    composable(Screen.Carrinho.route) { 
                        CarrinhoScreen(
                            viewModel = cartViewModel,
                            onBackClick = { navController.popBackStack() },
                            onFinalizarVenda = { /* Logica de finalizar */ }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(quantidadeItens: Int, onMenuClick: () -> Unit, onCartClick: () -> Unit) {
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
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Navigation drawer"
                )
            }
        },
        actions = {
            IconButton(onClick = onCartClick) {
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
fun DrawerContent(
    userName: String,
    companyName: String,
    currentRoute: String?,
    selectedTheme: Int,
    onThemeChange: (Int) -> Unit,
    onItemClick: (Screen) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = companyName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        drawerNavItems.forEach { screen ->
            NavigationDrawerItem(
                label = { Text(screen.title) },
                icon = { Icon(screen.icon, contentDescription = null) },
                selected = currentRoute == screen.route,
                onClick = { onItemClick(screen) },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        Text(
            text = "Aparência",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ThemeSwitcher(selectedTheme = selectedTheme, onThemeChange = onThemeChange)
    }
}

@Composable
fun ThemeSwitcher(selectedTheme: Int, onThemeChange: (Int) -> Unit) {
    val themes = listOf(
        Triple("Padrão", Icons.Default.Contrast, 0),
        Triple("Claro", Icons.Default.Brightness7, 1),
        Triple("Escuro", Icons.Default.Brightness4, 2)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        themes.forEach { (label, icon, index) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(if (selectedTheme == index) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                    .padding(8.dp)
                    .clickable { onThemeChange(index) }) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (selectedTheme == index) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selectedTheme == index) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


@Composable
fun ResumoCarrinho(quantidadeItens: Int, totalValor: Double, onVerCarrinhoClick: () -> Unit) {
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
                    text = "TOTAL: R$ ${String.format("%.2f", totalValor)}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Quantidade: $quantidadeItens",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Button(onClick = onVerCarrinhoClick) {
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
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                alwaysShowLabel = false
            )
        }
    }
}
