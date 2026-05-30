package com.anotasmart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//Componente base para telas com cabeçalho (voltar + título) e conteúdo rolável.
@Composable
fun StandardScreen(
    title: String,
    onBackClick: () -> Unit,
    headerActions: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp),
    content: LazyListScope.() -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = verticalArrangement,
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            // Cabeçalho Padronizado
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onBackClick() }
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        headerActions()
                    }
                }
            }
            
            // Conteúdo da Tela
            content()
        }
        
        // Barra Inferior Fixa (opcional)
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            bottomBar()
        }
    }
}
