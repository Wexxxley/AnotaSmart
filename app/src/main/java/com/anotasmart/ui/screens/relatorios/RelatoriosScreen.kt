package com.anotasmart.ui.screens.relatorios

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anotasmart.AnotaSmartApplication
import com.anotasmart.ui.components.StandardScreen
import com.anotasmart.ui.screens.relatorios.components.DetailRow
import com.anotasmart.ui.screens.relatorios.components.PeriodSelector
import com.anotasmart.ui.screens.relatorios.components.SummaryCard
import com.anotasmart.utils.formatSafe


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
        onBackClick = onBackClick,
        hasBottomBar = false
    ) {

        // SEÇÃO 1
        item {
            PeriodSelector(
                selected = periodoSelecionado,
                onSelected = { viewModel.setPeriodo(it) }
            )
        }

        item {
            SectionHeader("Resumo de Lucros")
        }
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (overview.lucroLiquido >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Lucro Líquido (Lucro - Despesas)",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (overview.lucroLiquido >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "R$ ${formatSafe(overview.lucroLiquido)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (overview.lucroLiquido >= 0) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                    )
                }
            }
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
                title = "Lucro Bruto",
                value = "R$ ${formatSafe(overview.lucroBruto)}",
                description = "Ganho bruto sobre os produtos/serviços",
                icon = Icons.Default.TrendingUp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // SEÇÃO 2
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader("Fluxo de Caixa")
        }

        item {
            DetailRow(
                title = "Recebimentos Realizados",
                value = "R$ ${formatSafe(overview.recebimentosRealizados)}",
                description = "Total de parcelas pagas no período",
                icon = Icons.Default.PriceCheck,
                color = Color(0xFF2E7D32)
            )
        }

        item {
            DetailRow(
                title = "Contas a Receber",
                value = "R$ ${formatSafe(overview.contasAReceber)}",
                description = "Tudo que ainda falta receber (total)",
                icon = Icons.Default.AccountBalanceWallet,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        item {
            DetailRow(
                title = "Inadimplência",
                value = "R$ ${formatSafe(overview.inadimplencia)}",
                description = "Parcelas vencidas e não pagas",
                icon = Icons.Default.Warning,
                color = Color(0xFFE65100)
            )
        }

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

                    ExplanationItem("Lucro Bruto", "É o total das vendas menos o custo dos produtos vendidos.")
                    ExplanationItem("Lucro Líquido", "É o lucro bruto menos as despesas gerais.")
                    ExplanationItem("Recebimentos Realizados", "Refere-se apenas ao que já foi efetivamente pago no período.")
                    ExplanationItem("Inadimplência", "Soma de todas as parcelas que já passaram da data de vencimento.")
                }
            }
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
fun ExplanationItem(label: String, text: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

