package com.timezy.widget.ui.screens.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.timezy.widget.data.model.SortOption
import com.timezy.widget.ui.components.DeleteConfirmDialog
import com.timezy.widget.ui.components.MidnightBottomNavigation
import com.timezy.widget.ui.components.NavigationTab
import com.timezy.widget.ui.theme.CountdownTheme

// Design Tokens - Clean Minimal Production Android UI (Theme-Adaptive)
private val SettingsBg: Color @Composable get() = CountdownTheme.colors.background
private val SettingsSurface: Color @Composable get() = CountdownTheme.colors.card
private val SettingsBorder: Color @Composable get() = CountdownTheme.colors.border
private val SettingsTextPrimary: Color @Composable get() = CountdownTheme.colors.textPrimary
private val SettingsTextSecondary: Color @Composable get() = CountdownTheme.colors.textSecondary
private val SettingsAccentGold: Color @Composable get() = CountdownTheme.colors.accent
private val SettingsDestructive: Color @Composable get() = CountdownTheme.colors.destructive

private data class ThemeOption(
    val id: String,
    val title: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToWidgets: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var sortExpanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.setNotificationsEnabled(isGranted)
    }

    BackHandler {
        onNavigateBack()
    }

    LaunchedEffect(uiState.dataCleared) {
        if (uiState.dataCleared) {
            snackbarHostState.showSnackbar("All event data has been cleared.")
            viewModel.onDataClearedHandled()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SettingsBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = SettingsTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SettingsTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SettingsBg
                ),
                windowInsets = WindowInsets.statusBars
            )
        },
        bottomBar = {
            MidnightBottomNavigation(
                currentTab = NavigationTab.SETTINGS,
                onTabSelected = { tab ->
                    when (tab) {
                        NavigationTab.HOME -> onNavigateToHome()
                        NavigationTab.CALENDAR -> onNavigateToCalendar()
                        NavigationTab.WIDGETS -> onNavigateToWidgets()
                        NavigationTab.SETTINGS -> { /* Current tab */ }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // 1. APPEARANCE
            // ==========================================
            Column {
                SectionTitle(title = "Appearance")

                val themeOptions = listOf(
                    ThemeOption("SYSTEM", "System Default"),
                    ThemeOption("DARK", "Midnight Dark"),
                    ThemeOption("LIGHT", "Light Mode")
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = SettingsSurface,
                    border = BorderStroke(1.dp, SettingsBorder)
                ) {
                    Column {
                        themeOptions.forEachIndexed { index, option ->
                            val isSelected = uiState.themeMode == option.id

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setThemeMode(option.id) }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = option.title,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) SettingsTextPrimary else SettingsTextSecondary
                                )

                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.setThemeMode(option.id) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = SettingsAccentGold,
                                        unselectedColor = SettingsBorder
                                    )
                                )
                            }

                            if (index < themeOptions.size - 1) {
                                HorizontalDivider(
                                    color = SettingsBorder,
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 2. PREFERENCES
            // ==========================================
            Column {
                SectionTitle(title = "Preferences")

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = SettingsSurface,
                    border = BorderStroke(1.dp, SettingsBorder)
                ) {
                    Column {
                        // Default Event Sorting
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = "Default Event Sorting",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = SettingsTextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            ExposedDropdownMenuBox(
                                expanded = sortExpanded,
                                onExpandedChange = { sortExpanded = !sortExpanded }
                            ) {
                                OutlinedTextField(
                                    value = uiState.defaultSort.displayName,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SettingsAccentGold,
                                        unfocusedBorderColor = SettingsBorder,
                                        focusedTextColor = SettingsTextPrimary,
                                        unfocusedTextColor = SettingsTextPrimary,
                                        focusedContainerColor = SettingsSurface,
                                        unfocusedContainerColor = SettingsSurface,
                                        focusedTrailingIconColor = SettingsAccentGold,
                                        unfocusedTrailingIconColor = SettingsTextSecondary
                                    ),
                                    modifier = Modifier
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                        .fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = sortExpanded,
                                    onDismissRequest = { sortExpanded = false },
                                    containerColor = SettingsSurface,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, SettingsBorder)
                                ) {
                                    SortOption.entries.forEach { option ->
                                        val isOptionSelected = uiState.defaultSort == option
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = option.displayName,
                                                    color = if (isOptionSelected) SettingsAccentGold else SettingsTextPrimary,
                                                    fontWeight = if (isOptionSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                    fontSize = 14.sp
                                                )
                                            },
                                            onClick = {
                                                viewModel.setDefaultSort(option)
                                                sortExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(
                            color = SettingsBorder,
                            thickness = 1.dp
                        )

                        // Live Seconds Counter
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setShowSeconds(!uiState.showSeconds) }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 16.dp)
                            ) {
                                Text(
                                    text = "Live Seconds Counter",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SettingsTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Show real-time seconds on countdowns",
                                    fontSize = 13.sp,
                                    color = SettingsTextSecondary
                                )
                            }
                            Switch(
                                checked = uiState.showSeconds,
                                onCheckedChange = { viewModel.setShowSeconds(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = if (CountdownTheme.colors.isDark) SettingsBg else Color.White,
                                    checkedTrackColor = SettingsAccentGold,
                                    uncheckedThumbColor = SettingsTextSecondary,
                                    uncheckedTrackColor = SettingsSurface,
                                    uncheckedBorderColor = SettingsBorder
                                )
                            )
                        }
                    }
                }
            }

            // ==========================================
            // 3. NOTIFICATIONS
            // ==========================================
            Column {
                SectionTitle(title = "Notifications")

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = SettingsSurface,
                    border = BorderStroke(1.dp, SettingsBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val nextState = !uiState.notificationsEnabled
                                if (nextState) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) != PackageManager.PERMISSION_GRANTED
                                    ) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        viewModel.setNotificationsEnabled(true)
                                    }
                                } else {
                                    viewModel.setNotificationsEnabled(false)
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 16.dp)
                        ) {
                            Text(
                                text = "Event Reminders",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = SettingsTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Remind me before events and on the event day.",
                                fontSize = 13.sp,
                                color = SettingsTextSecondary
                            )
                        }
                        Switch(
                            checked = uiState.notificationsEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) != PackageManager.PERMISSION_GRANTED
                                    ) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        viewModel.setNotificationsEnabled(true)
                                    }
                                } else {
                                    viewModel.setNotificationsEnabled(false)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = if (CountdownTheme.colors.isDark) SettingsBg else Color.White,
                                checkedTrackColor = SettingsAccentGold,
                                uncheckedThumbColor = SettingsTextSecondary,
                                uncheckedTrackColor = SettingsSurface,
                                uncheckedBorderColor = SettingsBorder
                            )
                        )
                    }
                }
            }

            // ==========================================
            // 4. ABOUT
            // ==========================================
            Column {
                SectionTitle(title = "About")

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = SettingsSurface,
                    border = BorderStroke(1.dp, SettingsBorder)
                ) {
                    Column {
                        // Version
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Version",
                                fontSize = 15.sp,
                                color = SettingsTextPrimary
                            )
                            Text(
                                text = "1.0.0",
                                fontSize = 14.sp,
                                color = SettingsTextSecondary
                            )
                        }

                        HorizontalDivider(
                            color = SettingsBorder,
                            thickness = 1.dp
                        )

                        // Storage
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Storage",
                                fontSize = 15.sp,
                                color = SettingsTextPrimary
                            )
                            Text(
                                text = "On-device / Offline",
                                fontSize = 14.sp,
                                color = SettingsTextSecondary
                            )
                        }

                        HorizontalDivider(
                            color = SettingsBorder,
                            thickness = 1.dp
                        )

                        // Concise privacy line
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your events stay on your device.",
                                fontSize = 13.sp,
                                color = SettingsTextSecondary
                            )
                        }
                    }
                }
            }

            // ==========================================
            // 5. DATA MANAGEMENT
            // ==========================================
            Column {
                SectionTitle(title = "Data Management")

                OutlinedButton(
                    onClick = { viewModel.showClearDataDialog() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SettingsDestructive.copy(alpha = 0.45f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SettingsDestructive
                    )
                ) {
                    Text(
                        text = "Clear All Event Data",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = SettingsDestructive
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (uiState.showClearDataDialog) {
        DeleteConfirmDialog(
            itemTitle = "All Events",
            titleText = "Clear All Event Data?",
            messageText = "This will permanently delete all events and reminders. Are you sure you want to proceed?",
            onConfirm = { viewModel.clearAllData() },
            onDismiss = { viewModel.dismissClearDataDialog() }
        )
    }
}

@Composable
private fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = SettingsTextSecondary,
        modifier = modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}
