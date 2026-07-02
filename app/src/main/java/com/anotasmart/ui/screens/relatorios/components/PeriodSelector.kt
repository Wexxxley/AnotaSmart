package com.anotasmart.ui.screens.relatorios.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anotasmart.model.PeriodoRelatorio


@Composable
fun PeriodSelector(
    selected: PeriodoRelatorio,
    onSelected: (PeriodoRelatorio) -> Unit
) {
    PrimaryScrollableTabRow(
        selectedTabIndex = selected.ordinal,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        divider = {}
    ) {
        PeriodoRelatorio.entries.forEach { periodo ->
            val isSelected = selected == periodo
            Tab(
                selected = isSelected,
                onClick = { onSelected(periodo) },
                text = {
                    Text(
                        text = when (periodo) {
                            PeriodoRelatorio.HOJE -> "Hoje"
                            PeriodoRelatorio.SEMANA -> "Semana"
                            PeriodoRelatorio.MES -> "Mês"
                            PeriodoRelatorio.ANO -> "Ano"
                            PeriodoRelatorio.TUDO -> "Tudo"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedContentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}
