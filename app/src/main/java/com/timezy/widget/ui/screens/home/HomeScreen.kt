package com.timezy.widget.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timezy.widget.R
import com.timezy.widget.data.model.CountdownEvent
import com.timezy.widget.data.model.FilterOption
import com.timezy.widget.data.model.SortOption
import com.timezy.widget.ui.components.CategoryChipRow
import com.timezy.widget.ui.components.DeleteConfirmDialog
import com.timezy.widget.ui.components.EmptyEventsView
import com.timezy.widget.ui.components.EventCard
import com.timezy.widget.ui.components.EventListItem
import com.timezy.widget.ui.components.FeaturedCountdownCard
import com.timezy.widget.ui.components.MidnightBottomNavigation
import com.timezy.widget.ui.components.NavigationTab
import com.timezy.widget.ui.theme.GoldCardBorder
import com.timezy.widget.ui.theme.GoldPrimaryAccent
import com.timezy.widget.ui.theme.MidnightAccentBackground
import com.timezy.widget.ui.theme.MidnightBackground
import com.timezy.widget.ui.theme.MidnightCard
import com.timezy.widget.ui.theme.MidnightMutedText
import com.timezy.widget.ui.theme.MidnightPrimaryAccent
import com.timezy.widget.ui.theme.MidnightPrimaryText
import com.timezy.widget.ui.theme.MidnightSecondarySurface
import com.timezy.widget.ui.theme.MidnightSecondaryText
import com.timezy.widget.ui.theme.MidnightSubtleBorder

enum class EventViewMode {
    CARDS,
    LIST
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAddEvent: () -> Unit,
    onNavigateToEditEvent: (Long) -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToCustomization: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val nowMillis by viewModel.nowMillis.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }
    var viewMode by remember { mutableStateOf(EventViewMode.CARDS) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MidnightBackground,
        bottomBar = {
            MidnightBottomNavigation(
                currentTab = NavigationTab.HOME,
                onTabSelected = { tab ->
                    when (tab) {
                        NavigationTab.HOME -> {}
                        NavigationTab.CALENDAR -> onNavigateToCalendar()
                        NavigationTab.WIDGETS -> onNavigateToCustomization()
                        NavigationTab.SETTINGS -> onNavigateToSettings()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddEvent,
                containerColor = GoldPrimaryAccent,
                contentColor = Color(0xFF171923),
                shape = CircleShape,
                modifier = Modifier.size(58.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Countdown Event",
                    tint = Color(0xFF171923),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // ==========================================
            // 1. HEADER (Section 3)
            // ==========================================
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Luxurious clock app logo
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, MidnightSubtleBorder, RoundedCornerShape(14.dp))
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Timezy",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    letterSpacing = (-0.3).sp
                                ),
                                color = MidnightPrimaryText
                            )
                            Text(
                                text = "Widget & Reminders",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    color = MidnightSecondaryText,
                                    letterSpacing = 0.2.sp
                                )
                            )
                        }
                    }

                    // Theme customization and settings circular action buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MidnightCard)
                                .border(1.dp, MidnightSubtleBorder, CircleShape)
                                .clickable { onNavigateToCustomization() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Theme Customization",
                                tint = MidnightPrimaryText,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MidnightCard)
                                .border(1.dp, MidnightSubtleBorder, CircleShape)
                                .clickable { onNavigateToSettings() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MidnightPrimaryText,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }

            // ==========================================
            // 2. SEARCH AND FILTER SECTION (Section 4)
            // ==========================================
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = {
                            Text(
                                "Search countdowns...",
                                color = MidnightMutedText,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MidnightMutedText,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = MidnightSecondaryText,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(26.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MidnightPrimaryText,
                            unfocusedTextColor = MidnightPrimaryText,
                            focusedBorderColor = GoldPrimaryAccent,
                            unfocusedBorderColor = MidnightSubtleBorder,
                            focusedContainerColor = MidnightCard,
                            unfocusedContainerColor = MidnightCard
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Compact Sort / Filter Button
                    Box {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MidnightCard)
                                .border(1.dp, MidnightSubtleBorder, RoundedCornerShape(20.dp))
                                .clickable { showSortMenu = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "Sort Options",
                                tint = MidnightSecondaryText,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                            modifier = Modifier.background(MidnightCard)
                        ) {
                            SortOption.entries.forEach { sort ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = sort.displayName,
                                            fontWeight = if (sort == uiState.sortOption) FontWeight.Bold else FontWeight.Normal,
                                            color = if (sort == uiState.sortOption) GoldPrimaryAccent else MidnightPrimaryText
                                        )
                                    },
                                    onClick = {
                                        viewModel.onSortChange(sort)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Filter Tabs Row
            item {
                Spacer(modifier = Modifier.height(8.dp))
                CategoryChipRow(
                    selectedFilter = uiState.selectedFilter,
                    onFilterSelected = { viewModel.onFilterSelect(it) },
                    counts = uiState.counts
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Featured Countdown Card (Shown when appropriate)
            if (uiState.featuredEvent != null && uiState.selectedFilter == FilterOption.ALL && uiState.searchQuery.isBlank()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp, start = 2.dp, end = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "FEATURED COUNTDOWN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    fontSize = 11.sp
                                ),
                                color = MidnightMutedText
                            )

                            // Carousel < and > Navigation Buttons
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(MidnightCard)
                                        .border(1.dp, MidnightSubtleBorder, CircleShape)
                                        .clickable { viewModel.onPrevFeatured() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                        contentDescription = "Previous Featured",
                                        tint = MidnightSecondaryText,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(MidnightCard)
                                        .border(1.dp, MidnightSubtleBorder, CircleShape)
                                        .clickable { viewModel.onNextFeatured() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Next Featured",
                                        tint = MidnightSecondaryText,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        FeaturedCountdownCard(
                            event = uiState.featuredEvent!!,
                            onCardClick = { onNavigateToDetail(uiState.featuredEvent!!.id) },
                            onToggleFavorite = { viewModel.toggleFavorite(uiState.featuredEvent!!) },
                            nowMillis = nowMillis,
                            showSeconds = uiState.showSeconds
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }

            // ==========================================
            // 3. UPCOMING EVENTS HEADER & VIEW SELECTOR (Section 5)
            // ==========================================
            if (uiState.upcomingEvents.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (uiState.selectedFilter) {
                                FilterOption.ALL -> "UPCOMING EVENTS"
                                FilterOption.UPCOMING -> "UPCOMING EVENTS"
                                FilterOption.TODAY -> "HAPPENING TODAY"
                                FilterOption.PAST -> "PAST EVENTS"
                                FilterOption.FAVORITES -> "FAVORITE EVENTS"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                fontSize = 11.sp
                            ),
                            color = MidnightMutedText
                        )

                        // Segmented View Selector: Cards | List
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MidnightCard)
                                .border(1.dp, MidnightSubtleBorder, RoundedCornerShape(20.dp))
                                .padding(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Cards Option (Selected in screenshot)
                            val isCards = viewMode == EventViewMode.CARDS
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isCards) GoldPrimaryAccent else Color.Transparent)
                                    .clickable { viewMode = EventViewMode.CARDS }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GridView,
                                    contentDescription = null,
                                    tint = if (isCards) Color(0xFF171923) else MidnightSecondaryText,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Cards",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isCards) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    ),
                                    color = if (isCards) Color(0xFF171923) else MidnightSecondaryText
                                )
                            }

                            // List Option
                            val isList = viewMode == EventViewMode.LIST
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isList) GoldPrimaryAccent else Color.Transparent)
                                    .clickable { viewMode = EventViewMode.LIST }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.List,
                                    contentDescription = null,
                                    tint = if (isList) Color(0xFF171923) else MidnightSecondaryText,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "List",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isList) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    ),
                                    color = if (isList) Color(0xFF171923) else MidnightSecondaryText
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 4. EVENT CARDS & LIST ITEMS (Section 6)
            // ==========================================
            if (uiState.upcomingEvents.isNotEmpty()) {
                if (viewMode == EventViewMode.CARDS) {
                    items(uiState.upcomingEvents, key = { it.id }) { event ->
                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                            EventCard(
                                event = event,
                                onCardClick = { onNavigateToDetail(event.id) },
                                onToggleFavorite = { viewModel.toggleFavorite(event) },
                                onEditClick = { onNavigateToEditEvent(event.id) },
                                onDeleteClick = { viewModel.requestDelete(event) },
                                nowMillis = nowMillis
                            )
                        }
                    }
                } else {
                    items(uiState.upcomingEvents, key = { it.id }) { event ->
                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                            EventListItem(
                                event = event,
                                onItemClick = { onNavigateToDetail(event.id) },
                                onToggleFavorite = { viewModel.toggleFavorite(event) },
                                onEditClick = { onNavigateToEditEvent(event.id) },
                                onDeleteClick = { viewModel.requestDelete(event) },
                                nowMillis = nowMillis
                            )
                        }
                    }
                }
            } else if (uiState.featuredEvent == null || uiState.selectedFilter != FilterOption.ALL || uiState.searchQuery.isNotBlank()) {
                // Empty state
                val hasZeroEvents = (uiState.counts[FilterOption.ALL] ?: 0) == 0 && uiState.searchQuery.isBlank()
                val emptyTitle = when {
                    uiState.searchQuery.isNotBlank() -> "No matching countdowns"
                    hasZeroEvents -> "No countdowns yet"
                    else -> "No events in this category"
                }
                val emptySubtitle = when {
                    uiState.searchQuery.isNotBlank() -> "Try searching for a different keyword or create a new countdown."
                    hasZeroEvents -> "Create your first countdown and start looking forward to something."
                    else -> "Tap below to add a new event to this list!"
                }
                item {
                    EmptyEventsView(
                        onAddClick = onNavigateToAddEvent,
                        title = emptyTitle,
                        subtitle = emptySubtitle,
                        buttonText = "+ Create Countdown"
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (uiState.eventToDelete != null) {
        DeleteConfirmDialog(
            itemTitle = uiState.eventToDelete!!.title,
            onConfirm = { viewModel.confirmDelete() },
            onDismiss = { viewModel.dismissDelete() }
        )
    }
}
