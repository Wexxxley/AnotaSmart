package com.anotasmart.ui.screens.vendas.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anotasmart.ui.components.CampoMoeda
import com.anotasmart.utils.PixUtils

@Composable
fun DialogConfirmacaoVenda(
    totalVenda: Double,
    isPix: Boolean = false,
    pixKey: String = "",
    companyName: String = "",
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

                if (isPix && pixKey.isNotBlank()) {
                    val pixPayload = remember(pixKey, companyName) {
                        PixUtils.generatePixPayload(pixKey, companyName, "SAO PAULO")
                    }
                    val qrBitmap = remember(pixPayload) {
                        PixUtils.generateQRCode(pixPayload, 400)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Pague com Pix",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            qrBitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "QR Code Pix",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } ?: CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = pixKey,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
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
