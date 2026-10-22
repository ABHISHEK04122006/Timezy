package com.timezy.widget

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.timezy.widget.ui.navigation.AppNavHost
import com.timezy.widget.ui.navigation.Screen
import com.timezy.widget.ui.theme.CountdownTheme
import com.timezy.widget.ui.theme.CountdownWidgetTheme

class MainActivity : ComponentActivity() {

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // Permission result handled
        }

    private val targetEventIdState = mutableStateOf<Long?>(-1L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request POST_NOTIFICATIONS permission on Android 13+
        checkAndRequestNotificationPermission()

        val appPreferences = (application as CountdownApplication).preferences
        val initialTargetId = intent?.getLongExtra(EXTRA_NAVIGATE_TO_EVENT_ID, -1L) ?: -1L
        targetEventIdState.value = initialTargetId

        setContent {
            val initialTheme = remember { appPreferences.getInitialThemeMode() }
            val themeMode by appPreferences.themeModeFlow.collectAsState(initial = initialTheme)
            val isDarkTheme = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            CountdownWidgetTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CountdownTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    val targetEventId by targetEventIdState

                    // If opened from a widget or notification, navigate to Detail with Home in backstack
                    LaunchedEffect(targetEventId) {
                        val id = targetEventId
                        if (id != null && id != -1L) {
                            navController.navigate(Screen.Detail.createRoute(id)) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                            targetEventIdState.value = -1L
                        }
                    }

                    val startDestination = if (initialTargetId != -1L) {
                        Screen.Home.route
                    } else {
                        Screen.Splash.route
                    }

                    AppNavHost(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        com.timezy.widget.widget.WidgetUpdateHelper.updateAllWidgets(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val newTargetId = intent.getLongExtra(EXTRA_NAVIGATE_TO_EVENT_ID, -1L)
        if (newTargetId != -1L) {
            targetEventIdState.value = newTargetId
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    companion object {
        const val EXTRA_NAVIGATE_TO_EVENT_ID = "extra_navigate_to_event_id"
    }
}
