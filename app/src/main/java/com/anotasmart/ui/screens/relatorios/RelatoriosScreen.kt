package com.anotasmart.ui.screens.relatorios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anotasmart.AnotaSmartApplication
import com.anotasmart.ui.components.StandardScreen
import com.anotasmart.ui.viewModels.FinancialOverviewState
import com.anotasmart.ui.viewModels.PeriodoRelatorio
import com.anotasmart.ui.viewModels.RelatoriosViewModel
import com.anotasmart.ui.viewModels.RelatoriosViewModelFactory
import com.anotasmart.utils.formatCurrency

@Composable
fun RelatoriosScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val database = (context.applicationContext as AnotaSmartApplication).database
    val viewModel: RelatoriosViewModel = viewModel(
        factory = RelatoriosViewModelFactory(
            database.saleDao(), 
            database.expenseDao(),
            database.installmentDao()
        )
    )

    val overview by viewModel.financialOverview.collectAsState()
    val periodoSelecionado by viewModel.periodo.collectAsState()

    StandardScreen(
        title = "Relatórios Financeiros",
        onBackClick = onBackClick
    ) {
        // Seleção de Período
        item {
            PeriodSelector(
                selected = periodoSelecionado,
                onSelected = { viewModel.setPeriodo(it) }
            )
        }

        // --- SEÇÃO 1: RESULTADO LÍQUIDO ---
        item {
            SectionHeader("Resumo de Lucros")
        }

        item {
            MainProfitCard(overview.lucroLiquido)
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "Faturamento",
                    value = overview.faturamento,
                    description = "Total bruto vendido",
                    icon = Icons.Default.ArrowUpward,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Despesas",
                    value = overview.despesas,
                    description = "Gastos fixos e extras",
                    icon = Icons.Default.ArrowDownward,
                    color = Color(0xFFC62828),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            DetailRow(
                title = "Lucro Estimado (Itens)",
                value = formatCurrency(overview.lucroEstimado),
                description = "Ganho real sobre os produtos/serviços",
                icon = Icons.Default.TrendingUp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // --- SEÇÃO 2: FLUXO DE CAIXA ---
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader("Fluxo de Caixa (Entradas)")
        }

        item {
            DetailRow(
                title = "Dinheiro em Caixa",
                value = formatCurrency(overview.dinheiroEmCaixa),
                description = "Total de parcelas pagas no período",
                icon = Icons.Default.PriceCheck,
                color = Color(0xFF2E7D32)
            )
        }

        item {
            DetailRow(
                title = "Contas a Receber",
                value = formatCurrency(overview.contasAReceber),
                description = "Tudo que ainda falta receber (total)",
                icon = Icons.Default.AccountBalanceWallet,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        item {
            DetailRow(
                title = "Inadimplência",
                value = formatCurrency(overview.inadimplencia),
                description = "Parcelas vencidas e não pagas",
                icon = Icons.Default.Warning,
                color = Color(0xFFE65100)
            )
        }

        // --- OUTRAS INFORMAÇÕES ---
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader("Outros Dados")
        }

        item {
            DetailRow(
                title = "Volume de Vendas",
                value = "${overview.totalVendas} pedidos",
                description = "Quantidade de vendas no período",
                icon = Icons.Default.ShoppingCart,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            InfoExplanationCard()
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun InfoExplanationCard() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Entenda os conceitos:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            ExplanationItem("Lucro Líquido", "É o lucro das vendas menos as despesas gerais.")
            ExplanationItem("Dinheiro em Caixa", "Refere-se apenas ao que já foi efetivamente pago no período.")
            ExplanationItem("Inadimplência", "Soma de todas as parcelas que já passaram da data de vencimento.")
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Nota: O lucro das vendas usa o preço de custo registrado no ato da venda.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ExplanationItem(label: String, text: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun PeriodSelector(
    selected: PeriodoRelatorio,
    onSelected: (PeriodoRelatorio) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selected.ordinal,
        edgePadding = 0.dp,
        containerColor = Color.Transparent,
        divider = {},
        indicator = {}
    ) {
        PeriodoRelatorio.values().forEach { periodo ->
            val isSelected = selected == periodo
            Tab(
                selected = isSelected,
                onClick = { onSelected(periodo) },
                text = {
                    Text(
                        text = when (periodo) {
                            PeriodoRelatorio.HOJE -> "Hoje"
                            PeriodoRelatorio.SEMANA -> "Semana"
                            PeriodoRelatorio.MES -> "Mês"
                            PeriodoRelatorio.ANO -> "Ano"
                            PeriodoRelatorio.TUDO -> "Tudo"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

@Composable
fun MainProfitCard(lucro: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (lucro >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Lucro Líquido no Período",
                style = MaterialTheme.typography.labelLarge,
                color = if (lucro >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatCurrency(lucro),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = if (lucro >= 0) Color(0xFF1B5E20) else Color(0xFFB71C1C)
            )
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: Double,
    description: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = formatCurrency(value),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun DetailRow(
    title: String,
    value: String,
    description: String,
    icon: ImageVector,
    color: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
