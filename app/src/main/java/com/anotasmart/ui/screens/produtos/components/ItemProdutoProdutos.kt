package com.anotasmart.ui.screens.produtos.components

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.anotasmart.model.ItemType
import com.anotasmart.model.entity.Product

@SuppressLint("DefaultLocale")
@Composable
fun ItemProdutoProdutos(
    produto: Product,
    onClick: () -> Unit,
    onAddEstoqueClick: () -> Unit
) {
    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!produto.imagePath.isNullOrEmpty()) {
                        // Verifica se é um resource ID (inteiro) ou um caminho/URI
                        val imageModel: Any = produto.imagePath.toIntOrNull() ?: produto.imagePath
                        AsyncImage(
                            model = imageModel,
                            contentDescription = "Imagem do produto ${produto.nome}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = "Ícone de produto sem imagem",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = produto.nome,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = "R$ ${String.format("%.2f", produto.precoVenda)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (produto.tipoItem == ItemType.PRODUTO) "${produto.quantidadeEstoque} ${produto.unidadeMedida.name}" else " ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Botão flutuante no card para estoque (apenas se for PRODUTO)
        if (produto.tipoItem == ItemType.PRODUTO) {
            SmallFloatingActionButton(
                onClick = onAddEstoqueClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(28.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar estoque",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
