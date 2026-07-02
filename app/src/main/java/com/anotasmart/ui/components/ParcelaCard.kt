package com.anotasmart.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.anotasmart.model.SaleWithRelations
import com.anotasmart.model.entity.Client
import com.anotasmart.model.entity.Installment
import com.anotasmart.utils.formatDate
import com.anotasmart.utils.formatSafe

@Composable
fun ParcelaCard(
    installment: Installment,
    saleWithRelations: SaleWithRelations,
    client: Client?,
    onBaixa: (String) -> Unit
) {
    val hoje = System.currentTimeMillis()
    val estaAtrasada = installment.dataVencimento < hoje

    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    onBaixa(installment.id)
                    showConfirmDialog = false
                }) {
                    Text("CONFIRMAR")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("CANCELAR")
                }
            },
            title = { Text("Confirmar Pagamento") },
            text = {
                Text("Deseja registrar o pagamento da parcela ${installment.numeroParcela} no valor de R$ ${formatSafe(installment.valor)}?")
            }
        )
    }

    StandardItemCard(
        title = "Parcela ${installment.numeroParcela}",
        label = "Vencimento: ${formatDate(installment.dataVencimento)}",
        value = "R$ ${formatSafe(installment.valor)}",
        labelIcon = if (estaAtrasada) Icons.Default.Warning else null,
        labelColor = if (estaAtrasada) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        valueColor = if (estaAtrasada) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        backgroundColor = if (estaAtrasada) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerLowest,
        trailingContent = {
            IconButton(
                onClick = { showConfirmDialog = true },
                modifier = Modifier.size(40.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xFFE8F5E9),
                    contentColor = Color(0xFF2E7D32)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Dar Baixa",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
}
