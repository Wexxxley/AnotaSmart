package com.anotasmart.ui.screens.sobre

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anotasmart.R
import com.anotasmart.ui.components.StandardScreen
import androidx.core.net.toUri

@Composable
fun SobreScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    StandardScreen(
        title = "Sobre o App",
        onBackClick = onBackClick
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Foto (Redonda)
                Surface(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    tonalElevation = 4.dp,
                    shadowElevation = 8.dp
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.dev),
                        contentDescription = "Foto do Desenvolvedor",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Wesley Sobrinho",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Desenvolvedor",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "AnotaSmart",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text(
                            text = "Desenvolvido para simplificar a vida do microempreendedor e do trabalhador autônomo. " +
                                    "O foco é oferecer um controle financeiro e de vendas que seja rápido, prático e sem complicações.",
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 26.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botões de Ação
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW,
                                "https://github.com/Wexxxley".toUri())
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24292E))
                    ) {
                        Icon(imageVector = Icons.Default.Code, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Ver GitHub", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = "mailto:wesleyfr.sobrinho@gmail.com".toUri()
                                putExtra(Intent.EXTRA_SUBJECT, "AnotaSmart - Contato")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Entrar em Contato", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Versão 1.0.0",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}
