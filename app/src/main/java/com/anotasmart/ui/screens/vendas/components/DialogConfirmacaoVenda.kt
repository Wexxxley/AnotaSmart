package com.anotasmart.ui.screens.vendas.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.anotasmart.ui.components.CampoMoeda

@Composable
fun DialogConfirmacaoVenda(
    totalVenda: Double,
    onDismissRequest: () -> Unit,
    onConfirmar: () -> Unit
) {
    var valorRecebidoStr by remember { mutableStateOf("") }
    val valorRecebido = valorRecebidoStr.toDoubleOrNull() ?: 0.0
    val troco = if (valorRecebido > totalVenda) valorRecebido - totalVenda else 0.0

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Confirmar Venda",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Total a Pagar",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "R$ ${String.format("%.2f", totalVenda)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                CampoMoeda(
                    value = valorRecebidoStr,
                    onValueChange = { valorRecebidoStr = it },
                    label = "Valor Recebido (Opcional)",
                    modifier = Modifier.fillMaxWidth()
                )

                if (valorRecebido > totalVenda) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Troco",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "R$ ${String.format("%.2f", troco)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onConfirmar,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "CONFIRMAR",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}
