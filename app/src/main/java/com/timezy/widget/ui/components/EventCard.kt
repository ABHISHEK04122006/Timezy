package com.timezy.widget.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timezy.widget.data.model.CountdownEvent
import com.timezy.widget.data.model.RecurrenceType
import com.timezy.widget.ui.theme.MidnightAccentBackground
import com.timezy.widget.ui.theme.MidnightAnniversaryAccent
import com.timezy.widget.ui.theme.MidnightCard
import com.timezy.widget.ui.theme.MidnightMutedText
import com.timezy.widget.ui.theme.MidnightPrimaryAccent
import com.timezy.widget.ui.theme.MidnightPrimaryText
import com.timezy.widget.ui.theme.MidnightSecondarySurface
import com.timezy.widget.ui.theme.MidnightSecondaryText
import com.timezy.widget.ui.theme.MidnightSubtleBorder
import com.timezy.widget.utils.DateTimeUtils
import com.timezy.widget.widget.WidgetUpdateHelper

/**
 * Midnight Minimal Horizontal Event Card
 *
 * Distinct three-area layout:
 * - Left: Category icon thumbnail with rounded square container
 * - Middle: Event name, uppercase category, date with calendar icon
 * - Right: Remaining days rounded block & subtle action menu
 */
@Composable
fun EventCard(
    event: CountdownEvent,
    onCardClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    nowMillis: Long = System.currentTimeMillis()
) {
    val remaining = event.calculateRemainingTime(nowMillis)
    val cardShape = RoundedCornerShape(20.dp)
    val categoryAccent = event.category.getAccentColor()
    val containerBg = event.category.getContainerColor()
    val badgeBg = event.category.getBadgeColor()
    val borderColor = event.category.getBorderColor()
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var showWidgetPicker by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(MidnightCard)
            .border(width = 1.dp, color = MidnightSubtleBorder, shape = cardShape)
            .clickable { onCardClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT SECTION: Category Squircle Thumbnail
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(containerBg)
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = event.category.getIcon(),
                    contentDescription = event.category.displayName,
                    tint = categoryAccent,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // MIDDLE SECTION: Event title, uppercase category, target date
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            letterSpacing = (-0.2).sp
                        ),
                        color = MidnightPrimaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (event.recurrence != RecurrenceType.NONE) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = "Recurring",
                            tint = MidnightPrimaryAccent,
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    if (event.isFavorite) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorite",
                            tint = if (com.timezy.widget.ui.theme.CountdownTheme.colors.isDark) Color(0xFFFFA6B8) else Color(0xFFE11D48),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Uppercase Category in accent color
                Text(
                    text = event.category.displayName.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp
                    ),
                    color = categoryAccent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Date with Calendar Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MidnightMutedText,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = DateTimeUtils.formatDate(event.getEffectiveTargetEpochMillis(nowMillis)) +
                                if (event.hasSpecificTime) " • " + DateTimeUtils.formatTime(event.getEffectiveTargetEpochMillis(nowMillis)) else "",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                        color = MidnightSecondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // RIGHT SECTION: Themed Days Countdown Block
            val daysBlockShape = RoundedCornerShape(16.dp)
            val badgeNumberColor = event.category.getBadgeNumberColor()
            val badgeLabelColor = event.category.getBadgeLabelColor()

            val numberFontSize = when {
                remaining.primaryNumberText.length >= 4 -> 15.sp
                remaining.primaryNumberText.length == 3 -> 17.sp
                else -> 19.sp
            }

            Column(
                modifier = Modifier
                    .widthIn(min = 72.dp)
                    .height(58.dp)
                    .clip(daysBlockShape)
                    .background(badgeBg)
                    .border(width = 1.dp, color = borderColor, shape = daysBlockShape)
                    .padding(vertical = 5.dp, horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = remaining.primaryNumberText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = numberFontSize,
                        letterSpacing = (-0.5).sp
                    ),
                    color = badgeNumberColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = remaining.primaryLabelText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = badgeLabelColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Subtle Overflow Menu Action (⋮)
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MidnightMutedText,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("Add Widget to Home", fontWeight = FontWeight.SemiBold)
                                Text(
                                    "Choose widget style to pin",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Widgets,
                                contentDescription = null,
                                tint = MidnightPrimaryAccent
                            )
                        },
                        onClick = {
                            showMenu = false
                            showWidgetPicker = true
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(if (event.isFavorite) "Remove Favorite" else "Mark as Favorite")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (event.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (event.isFavorite) Color(0xFFF17D91) else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            showMenu = false
                            onToggleFavorite()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Edit Event") },
                        leadingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        },
                        onClick = {
                            showMenu = false
                            onEditClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete Event", color = Color(0xFFEF4444)) },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444))
                        },
                        onClick = {
                            showMenu = false
                            onDeleteClick()
                        }
                    )
                }
            }
        }
    }

    if (showWidgetPicker) {
        WidgetTypePickerDialog(
            eventId = event.id,
            eventTitle = event.title,
            onDismiss = { showWidgetPicker = false }
        )
    }
}

/**
 * Midnight Minimal High-Density List Item for the "List" segmented view
 */
@Composable
fun EventListItem(
    event: CountdownEvent,
    onItemClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    nowMillis: Long = System.currentTimeMillis()
) {
    val remaining = event.calculateRemainingTime(nowMillis)
    val itemShape = RoundedCornerShape(16.dp)
    val categoryAccent = event.category.getAccentColor()
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var showWidgetPicker by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(itemShape)
            .background(MidnightCard)
            .border(width = 1.dp, color = MidnightSubtleBorder, shape = itemShape)
            .clickable { onItemClick() }
            .padding(horizontal = 14.dp, vertical = 11.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Pill
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(event.category.getContainerColor())
                    .border(1.dp, event.category.getBorderColor(), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = event.category.getIcon(),
                    contentDescription = null,
                    tint = categoryAccent,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title & Date
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    ),
                    color = MidnightPrimaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${event.category.displayName} • ${DateTimeUtils.formatDate(event.getEffectiveTargetEpochMillis(nowMillis))}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MidnightSecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Countdown tag
            Text(
                text = remaining.displayDaysText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = when {
                    remaining.isToday -> MidnightAnniversaryAccent
                    remaining.isPast -> MidnightMutedText
                    else -> MidnightPrimaryAccent
                }
            )

            // Menu
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MidnightMutedText,
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("Add Widget to Home", fontWeight = FontWeight.SemiBold)
                                Text(
                                    "Choose widget style to pin",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Widgets, contentDescription = null, tint = MidnightPrimaryAccent)
                        },
                        onClick = {
                            showMenu = false
                            showWidgetPicker = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (event.isFavorite) "Remove Favorite" else "Mark Favorite") },
                        leadingIcon = {
                            Icon(
                                imageVector = if (event.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (event.isFavorite) {
                                    if (com.timezy.widget.ui.theme.CountdownTheme.colors.isDark) Color(0xFFFFA6B8) else Color(0xFFE11D48)
                                } else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            showMenu = false
                            onToggleFavorite()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Edit Event") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        onClick = {
                            showMenu = false
                            onEditClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete Event", color = Color(0xFFEF4444)) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444)) },
                        onClick = {
                            showMenu = false
                            onDeleteClick()
                        }
                    )
                }
            }
        }
    }

    if (showWidgetPicker) {
        WidgetTypePickerDialog(
            eventId = event.id,
            eventTitle = event.title,
            onDismiss = { showWidgetPicker = false }
        )
    }
}
