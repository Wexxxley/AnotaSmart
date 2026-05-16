package com.anotasmart.ui.screens.vendas

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anotasmart.R
import com.anotasmart.ui.theme.onPrimaryLight
import com.anotasmart.ui.theme.primaryLight

@Composable
fun VendaSucessoScreen(
    onNovaVendaClick: () -> Unit
) {
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        try {
            val mediaPlayer = MediaPlayer.create(context, R.raw.som_venda)
            mediaPlayer?.apply {
                setVolume(0.5f, 0.5f)
                setOnCompletionListener { 
                    it.release() 
                }
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryLight),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = onPrimaryLight,
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Venda Realizada com Sucesso!",
                color = onPrimaryLight,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Os dados da venda foram salvos e o estoque foi atualizado.",
                color = onPrimaryLight.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = onNovaVendaClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = onPrimaryLight,
                    contentColor = primaryLight
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(
                    text = "NOVA VENDA",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }
        }
    }
}
