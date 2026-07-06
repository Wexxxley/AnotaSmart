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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness2
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.anotasmart.utils.formatSafe
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anotasmart.ui.navigation.Screen
import com.anotasmart.ui.navigation.bottomNavItems
import com.anotasmart.ui.navigation.drawerNavItems
import com.anotasmart.ui.screens.produtos.ProdutosScreen
import com.anotasmart.ui.screens.pedidos.PedidosScreen
import com.anotasmart.ui.screens.clientes.ClientesScreen
import com.anotasmart.ui.screens.clientes.ClienteDetalhesScreen
import com.anotasmart.ui.screens.despesas.DespesasScreen
import com.anotasmart.ui.screens.categorias.CategoriasScreen
import com.anotasmart.ui.screens.relatorios.RelatoriosScreen
import com.anotasmart.ui.screens.documentacao.DocumentacaoScreen
import com.anotasmart.ui.screens.chavepix.ChavePixScreen
import com.anotasmart.ui.screens.sobre.SobreScreen
import com.anotasmart.ui.screens.carrinho.CarrinhoScreen
import com.anotasmart.ui.screens.vendas.FinalizarVendaScreen
import com.anotasmart.ui.screens.vendas.VendaSucessoScreen
import com.anotasmart.ui.theme.AppTheme
import com.anotasmart.ui.screens.carrinho.CartViewModel
import com.anotasmart.ui.screens.carrinho.CartViewModelFactory
import com.anotasmart.ui.screens.setup.UserViewModel
import com.anotasmart.ui.screens.setup.UserViewModelFactory
import com.anotasmart.data.preferences.UserPreferencesRepository
import com.anotasmart.ui.screens.setup.SetupScreen
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.Brightness5
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anotasmart.ui.screens.clientes.ClientesViewModel
import com.anotasmart.ui.screens.clientes.ClientesViewModelFactory
import com.anotasmart.ui.screens.pedidos.PedidosViewModel
import com.anotasmart.ui.screens.pedidos.PedidosViewModelFactory
import kotlinx.coroutines.launch

val screensWithoutBottomBar = listOf(
    Screen.Carrinho.route,
    Screen.FinalizarVenda.route,
    Screen.ClienteDetalhes.route,
    Screen.VendaSucesso.route,
    Screen.Relatorios.route,
    Screen.Setup.route
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Contexto fornece a ligação entre o código e o ambiente do SO.
            // Provedor de acesso a recursos do sistema: arquivos, banco de dados e etc
            val context = LocalContext.current
            val userPrefsRepository = remember { UserPreferencesRepository(context) }
            val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userPrefsRepository))
            val userPrefs by userViewModel.userPreferences.collectAsState()

            val isDarkTheme = when (userPrefs.selectedTheme) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme() // retorna true or false
            }

            AppTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val db = (context.applicationContext as AnotaSmartApplication).database
                    val navController = rememberNavController() // Gerencia navegação
                    val cartViewModel: CartViewModel = viewModel(
                        factory = CartViewModelFactory(db.cartItemDao(), db.productDao())
                    )
                    ScreenStructure(
                        navController = navController,
                        cartViewModel = cartViewModel,
                        userViewModel = userViewModel
                    )
                }
            }
        }
    }
}

// utiliza State Hoisting para gerenciar três sistemas: o menu lateral, a barra de navegação e o conteúdo central.
@Composable
fun ScreenStructure(
    navController: NavHostController,
    cartViewModel: CartViewModel,
    userViewModel: UserViewModel
) {
    val userPrefs by userViewModel.userPreferences.collectAsState()
    val items by cartViewModel.items.collectAsState()
    val totalValor by cartViewModel.totalValor.collectAsState()
    val quantidadeItens = items.sumOf { it.quantidade }.toInt()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBars = currentRoute != Screen.Setup.route && currentRoute != Screen.Loading.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showBars,
        drawerContent = {
            ModalDrawerSheet (modifier = Modifier.width(300.dp)){
                DrawerContent(
                    userName = userPrefs.userName,
                    companyName = userPrefs.companyName,
                    profileImagePath = userPrefs.profileImagePath,
                    currentRoute = currentRoute,
                    selectedTheme = userPrefs.selectedTheme,
                    onThemeChange = { userViewModel.updateTheme(it) },
                    onItemClick = { screen ->
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                // Antes de ir, a pilha de navegação é limpa voltando até a tela inicial
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true // salva o estado de cada tela intermediária
                                }
                                launchSingleTop = true // impede cópias de telas iguais
                                restoreState = true // se a tela de destino já foi visitada, esse estado será restaurado.
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
                if (showBars && currentRoute != Screen.VendaSucesso.route) {
                    BarraSuperior(
                        quantidadeItens = quantidadeItens,
                        profileImagePath = userPrefs.profileImagePath,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onCartClick = { 
                            if (currentRoute != Screen.Carrinho.route) {
                                navController.navigate(Screen.Carrinho.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        onProfileClick = {
                            if (currentRoute != Screen.Setup.route) {
                                navController.navigate(Screen.Setup.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if ( currentRoute !in screensWithoutBottomBar) {
                    Column {
                        if (quantidadeItens > 0) {
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
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (!showBars || currentRoute == Screen.VendaSucesso.route) PaddingValues(0.dp) else innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // funciona como o catálogo central de destinos do aplicativo
                NavHost(
                    navController = navController,
                    startDestination = Screen.Loading.route
                ) {
                    composable(Screen.Loading.route) {
                        LaunchedEffect(userPrefs.isLoaded) {
                            if (userPrefs.isLoaded) {
                                val isSetupComplete = userPrefs.userName.isNotBlank() && 
                                                     userPrefs.companyName.isNotBlank() && 
                                                     !userPrefs.profileImagePath.isNullOrBlank()
                                
                                val destination = if (isSetupComplete) Screen.Loja.route else Screen.Setup.route
                                navController.navigate(destination) {
                                    popUpTo(Screen.Loading.route) { inclusive = true }
                                }
                            }
                        }
                    }
                    composable(Screen.Setup.route) {
                        SetupScreen(
                            userViewModel = userViewModel,
                            onComplete = {
                                navController.navigate(Screen.Loja.route) {
                                    popUpTo(Screen.Setup.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.Loja.route) { 
                        ProdutosScreen(cartViewModel = cartViewModel) 
                    }
                    composable(Screen.Pedidos.route) { 
                        val context = LocalContext.current
                        val db = (context.applicationContext as AnotaSmartApplication).database
                        val pedidosViewModel: PedidosViewModel = viewModel(
                            factory = PedidosViewModelFactory(db.saleDao(), db.installmentDao())
                        )
                        PedidosScreen(viewModel = pedidosViewModel) 
                    }
                    composable(Screen.Clientes.route) { 
                        ClientesScreen(
                            onClientClick = { cliente ->
                                navController.navigate(Screen.ClienteDetalhes.createRoute(cliente.id))
                            }
                        ) 
                    }
                    composable(Screen.Despesas.route) { DespesasScreen() }
                    composable(Screen.Categorias.route) { CategoriasScreen() }
                    composable(Screen.Relatorios.route) { 
                        RelatoriosScreen(onBackClick = { navController.popBackStack() }) 
                    }
                    composable(Screen.Tutoriais.route) { DocumentacaoScreen() }
                    composable(Screen.ChavePix.route) { ChavePixScreen() }
                    composable(Screen.Sobre.route) { 
                        SobreScreen(onBackClick = { navController.popBackStack() }) 
                    }
                    composable(Screen.Carrinho.route) { 
                        CarrinhoScreen(
                            viewModel = cartViewModel,
                            onBackClick = { navController.popBackStack() },
                            onFinalizarVenda = { 
                                navController.navigate(Screen.FinalizarVenda.route)
                            }
                        )
                    }
                    composable(Screen.FinalizarVenda.route) {
                        FinalizarVendaScreen(
                            onBackClick = { navController.popBackStack() },
                            onConfirmarVenda = {
                                navController.navigate(Screen.VendaSucesso.route) {
                                    popUpTo(Screen.Loja.route) { inclusive = false }
                                }
                            }
                        )
                    }
                    composable(Screen.VendaSucesso.route) {
                        VendaSucessoScreen(
                            onNovaVendaClick = {
                                navController.navigate(Screen.Loja.route) {
                                    popUpTo(Screen.Loja.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.ClienteDetalhes.route) { backStackEntry ->
                        val clientId = backStackEntry.arguments?.getString("clientId")
                        val context = LocalContext.current
                        val db = (context.applicationContext as AnotaSmartApplication).database
                        val clientViewModel: ClientesViewModel = viewModel(
                            factory = ClientesViewModelFactory(
                                db.clientDao(),
                                db.saleDao(),
                                db.installmentDao()
                            )
                        )
                        ClienteDetalhesScreen(
                            clientId = clientId,
                            viewModel = clientViewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    quantidadeItens: Int,
    profileImagePath: String? = null,
    onMenuClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                if (profileImagePath != null) {
                    AsyncImage(
                        model = profileImagePath,
                        contentDescription = "Perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil do Usuário",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
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
    profileImagePath: String?,
    currentRoute: String?,
    selectedTheme: Int,
    onThemeChange: (Int) -> Unit,
    onItemClick: (Screen) -> Unit
) {
    val scrollState = androidx.compose.foundation.rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
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
                if (profileImagePath != null) {
                    AsyncImage(
                        model = profileImagePath,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
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
        Triple("Claro", Icons.Default.Brightness5, 1),
        Triple("Escuro", Icons.Default.Brightness2, 2)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        themes.forEach { (label, icon, index) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onThemeChange(index) }
                    .background(if (selectedTheme == index) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                    .padding(8.dp) ){
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
                    text = "TOTAL: R$ ${formatSafe(totalValor)}",
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
