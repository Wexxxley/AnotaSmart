package com.anotasmart.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*

/**
 * Um diálogo de confirmação em duas etapas para ações destrutivas (como exclusão).
 */
@Composable
fun DoubleDeleteConfirmationDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    title: String = "Confirmar Exclusão",
    message1: String,
    message2: String = "Esta ação não pode ser desfeita. Todos os dados relacionados serão removidos permanentemente. Confirmar?",
    confirmButtonText: String = "Sim, Apagar Definitivamente"
) {
    var showSecondStep by remember(showDialog) { mutableStateOf(false) }

    if (!showDialog) return

    if (!showSecondStep) {
        // Primeira Etapa
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(title) },
            text = { Text(message1) },
            confirmButton = {
                TextButton(onClick = { showSecondStep = true }) {
                    Text("Sim", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancelar")
                }
            }
        )
    } else {
        // Segunda Etapa (Confirmação Final)
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("Confirmação Final") },
            text = { Text(message2) },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm()
                        onDismissRequest() // Fecha após confirmar
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSecondStep = false }) {
                    Text("Não, Voltar")
                }
            }
        )
    }
}
