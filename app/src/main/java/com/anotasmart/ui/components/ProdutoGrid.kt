package com.anotasmart.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anotasmart.model.entity.Product

@Composable
fun ProdutoGrid(
    produtos: List<Product>,
    isManagementMode: Boolean,
    onProdutoClick: (Product) -> Unit,
    onAddEstoqueClick: ((Product) -> Unit)? = null
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(produtos) { produto ->
            ItemCard(
                produto = produto,
                isManagementMode = isManagementMode,
                onClick = { onProdutoClick(produto) },
                onAddEstoqueClick = if (onAddEstoqueClick != null) { { onAddEstoqueClick(produto) } } else null
            )
        }
    }
}
