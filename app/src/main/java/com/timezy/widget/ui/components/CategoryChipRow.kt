package com.timezy.widget.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timezy.widget.data.model.FilterOption
import com.timezy.widget.ui.theme.MidnightBackground
import com.timezy.widget.ui.theme.MidnightCard
import com.timezy.widget.ui.theme.MidnightMutedText
import com.timezy.widget.ui.theme.MidnightPrimaryAccent
import com.timezy.widget.ui.theme.MidnightSecondarySurface
import com.timezy.widget.ui.theme.MidnightSecondaryText
import com.timezy.widget.ui.theme.MidnightSubtleBorder

@Composable
fun CategoryChipRow(
    selectedFilter: FilterOption,
    onFilterSelected: (FilterOption) -> Unit,
    counts: Map<FilterOption, Int>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(FilterOption.entries) { filter ->
            val isSelected = filter == selectedFilter
            val count = counts[filter] ?: 0
            val shape = RoundedCornerShape(24.dp)

            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(if (isSelected) MidnightPrimaryAccent else MidnightCard)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) MidnightPrimaryAccent else MidnightSubtleBorder,
                        shape = shape
                    )
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = filter.displayName,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        ),
                        color = if (isSelected) Color(0xFF171923) else MidnightSecondaryText
                    )

                    if (count > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) Color.Black.copy(alpha = 0.14f) else MidnightSecondarySurface
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = count.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (isSelected) Color(0xFF171923) else MidnightMutedText
                            )
                        }
                    }
                }
            }
        }
    }
}
