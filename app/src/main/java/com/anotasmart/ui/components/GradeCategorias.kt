package com.anotasmart.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import com.anotasmart.model.entity.Category

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GradeCategorias(
    categorias: List<Category>,
    selectedCategoryId: String? = null,
    onCategorySelected: ((String?) -> Unit)? = null,
    onAddCategoryClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        if (onAddCategoryClick != null) {
            AssistChip(
                onClick = onAddCategoryClick,
                label = { Text("Nova") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                    )
                }
            )
        }

        categorias.filter { it.id != "1" }.forEach { categoria ->
            val isSelected = selectedCategoryId == categoria.id
            if (onCategorySelected != null) {
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(if (isSelected) null else categoria.id) },
                    label = { 
                        Text(
                            text = categoria.nome,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        ) 
                    }
                )
            } else {
                SuggestionChip(
                    onClick = { },
                    label = { 
                        Text(
                            text = categoria.nome,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        ) 
                    }
                )
            }
        }
    }
}
