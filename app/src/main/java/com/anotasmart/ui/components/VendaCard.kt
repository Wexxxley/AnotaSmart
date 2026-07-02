package com.anotasmart.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anotasmart.model.InstallmentStatus
import com.anotasmart.model.PaymentMethod
import com.anotasmart.model.SaleWithRelations
import com.anotasmart.utils.formatDate
import com.anotasmart.utils.formatSafe

@Composable
fun VendaCard(vendaWithRelations: SaleWithRelations) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp, 12.dp)) {
            VendaHeader(vendaWithRelations, expanded)

            AnimatedVisibility(visible = expanded) {
                VendaItemsList(vendaWithRelations)
            }

            VendaFooter(vendaWithRelations)
        }
    }
}

@Composable
private fun VendaHeader(vendaWithRelations: SaleWithRelations, expanded: Boolean) {
    val venda = vendaWithRelations.sale
    val client = vendaWithRelations.client

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = client?.nome ?: "Cliente Avulso",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formatDate(venda.dataVenda),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "R$ ${formatSafe(venda.valorTotal)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun VendaItemsList(vendaWithRelations: SaleWithRelations) {
    Column {
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
        vendaWithRelations.items.forEach { itemWithProduct ->
            val item = itemWithProduct.saleItem
            val product = itemWithProduct.product

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val qtdFormatada = if (item.quantidade % 1.0 == 0.0) item.quantidade.toInt().toString() else item.quantidade.toString()
                val unidade = product?.unidadeMedida?.name?.lowercase() ?: ""
                val nome = item.nomeCustomizado ?: product?.nome ?: "Item"

                Text(
                    text = "$qtdFormatada $unidade $nome",
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "R$ ${formatSafe(item.precoVendaNoAto * item.quantidade)}"   ,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
    }
}

@Composable
private fun VendaFooter(vendaWithRelations: SaleWithRelations) {
    val installments = vendaWithRelations.installments

    // Lógica simplificada de Status
    val isParcelado = installments.size > 1 || installments.any { it.metodoPagamento == PaymentMethod.PARCELADO }
    val pagas = installments.count { it.statusParcela == InstallmentStatus.PAGA }
    val total = installments.size
    val finalizada = total > 0 && pagas == total
    val temAtraso = installments.any {
        it.statusParcela == InstallmentStatus.ATRASADA ||
                (it.statusParcela == InstallmentStatus.PENDENTE && it.dataVencimento < System.currentTimeMillis())
    }

    // Se for parcelado, mostra a barra de progresso
    if (isParcelado && !finalizada) {
        Column(modifier = Modifier.padding(top = 8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Progresso: $pagas/$total pagas", fontSize = 12.sp)
                if (temAtraso) {
                    Text("ATRASADA", fontSize = 10.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.error)
                }
            }
            LinearProgressIndicator(
                progress = { if (total > 0) pagas.toFloat() / total else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .padding(top = 4.dp),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
    // Se não for parcelado, mostra dinheiro ou pix
    else {
        val label = when {
            isParcelado -> "PARCELADO"
            installments.isNotEmpty() -> installments.first().metodoPagamento?.name ?: "À VISTA"
            else -> vendaWithRelations.sale.status.name
        }

        SuggestionChip(
            onClick = { },
            label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            modifier = Modifier.height(24.dp),
            colors = SuggestionChipDefaults.suggestionChipColors(
                containerColor = Color(0xFFE8F5E9),
                labelColor = Color(0xFF2E7D32)
            ),
            border = null
        )
    }
}
