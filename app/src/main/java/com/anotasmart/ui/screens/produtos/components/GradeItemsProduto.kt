package com.anotasmart.ui.screens.produtos.components

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
import com.anotasmart.ui.components.ItemCard

@Composable
fun GradeItemsProduto(
    produtos: List<Product>,
    onProdutoClick: (Product) -> Unit,
    onAddEstoqueClick: (Product) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(produtos) { produto ->
            ItemCard(
                produto = produto,
                onClick = { onProdutoClick(produto) },
                onAddEstoqueClick = { onAddEstoqueClick(produto) }
            )
        }
    }
}
