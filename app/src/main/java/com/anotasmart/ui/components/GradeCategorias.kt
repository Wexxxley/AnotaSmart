package com.anotasmart.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anotasmart.model.entity.Category

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GradeCategorias(
    categorias: List<Category>,
    selectedCategoryId: String? = null,
    onCategorySelected: ((String?) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        categorias.filter { it.id != "1" }.forEach { categoria ->
            val isSelected = selectedCategoryId == categoria.id
            if (onCategorySelected != null) {
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(if (isSelected) null else categoria.id) },
                    label = { Text(categoria.nome) }
                )
            } else {
                SuggestionChip(
                    onClick = { },
                    label = { Text(categoria.nome) }
                )
            }
        }
    }
}
