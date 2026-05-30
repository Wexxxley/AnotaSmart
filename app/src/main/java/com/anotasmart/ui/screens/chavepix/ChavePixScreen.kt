package com.anotasmart.ui.screens.chavepix

import android.R
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anotasmart.data.preferences.UserPreferencesRepository
import com.anotasmart.ui.viewModels.UserViewModel
import com.anotasmart.ui.viewModels.UserViewModelFactory
import com.anotasmart.utils.PixUtils

@Composable
fun ChavePixScreen() {
    val context = LocalContext.current
    val repository = remember { UserPreferencesRepository(context) }
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(repository))
    val userPreferences by userViewModel.userPreferences.collectAsState()
    
    val clipboardManager = LocalClipboardManager.current
    var isEditing by remember { mutableStateOf(false) }
    var tempPixKey by remember { mutableStateOf("") }

    LaunchedEffect(userPreferences.pixKey) {
        tempPixKey = userPreferences.pixKey
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (userPreferences.pixKey.isBlank() || isEditing) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.QrCode2,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = if (isEditing) "Editar Chave Pix" else "Configurar Chave Pix",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Informe sua chave Pix para gerar o QR Code de pagamento para seus clientes.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    OutlinedTextField(
                        value = tempPixKey,
                        onValueChange = { tempPixKey = it },
                        label = { Text("Chave Pix (E-mail, CPF, CNPJ, Celular)", fontSize = 12.sp)  },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            userViewModel.updatePixKey(tempPixKey)
                            isEditing = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        enabled = tempPixKey.isNotBlank()
                    ) {
                        Text("Salvar Chave")
                    }
                    
                    if (isEditing) {
                        TextButton(
                            onClick = { isEditing = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        } else {
            // Exibir QR Code
            val pixPayload = remember(userPreferences.pixKey, userPreferences.companyName) {
                PixUtils.generatePixPayload(
                    userPreferences.pixKey,
                    userPreferences.companyName,
                    "SAO PAULO" // Cidade padrão, poderia vir das prefs
                )
            }
            
            val qrBitmap = remember(pixPayload) {
                PixUtils.generateQRCode(pixPayload, 600)
            }

            Text(
                text = "Receber via Pix",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = userPreferences.companyName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                qrBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "QR Code Pix",
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sua Chave Pix",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = userPreferences.pixKey,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    IconButton(onClick = {
                        clipboardManager.setText(AnnotatedString(userPreferences.pixKey))
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copiar Chave")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = { isEditing = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Alterar Chave")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Este é um QR Code estático para recebimentos rápidos.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
