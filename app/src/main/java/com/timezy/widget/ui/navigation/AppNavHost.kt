package com.timezy.widget.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.timezy.widget.ui.screens.add_edit.AddEditEventScreen
import com.timezy.widget.ui.screens.add_edit.AddEditEventViewModel
import com.timezy.widget.ui.screens.calendar.CalendarScreen
import com.timezy.widget.ui.screens.customization.CustomizationViewModel
import com.timezy.widget.ui.screens.customization.WidgetCustomizationScreen
import com.timezy.widget.ui.screens.detail.EventDetailScreen
import com.timezy.widget.ui.screens.detail.EventDetailViewModel
import com.timezy.widget.ui.screens.home.HomeScreen
import com.timezy.widget.ui.screens.home.HomeViewModel
import com.timezy.widget.ui.screens.settings.SettingsScreen
import com.timezy.widget.ui.screens.settings.SettingsViewModel
import com.timezy.widget.ui.screens.splash.SplashScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(280)
            ) + fadeIn(animationSpec = tween(280))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(280)
            ) + fadeOut(animationSpec = tween(280))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(280)
            ) + fadeIn(animationSpec = tween(280))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(280)
            ) + fadeOut(animationSpec = tween(280))
        }
    ) {
        // Animated Splash Screen
        composable(
            route = Screen.Splash.route,
            enterTransition = { fadeIn(tween(250)) },
            exitTransition = { fadeOut(tween(300)) }
        ) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel()
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToAddEvent = {
                    navController.navigate(Screen.AddEvent.route)
                },
                onNavigateToEditEvent = { eventId ->
                    navController.navigate(Screen.EditEvent.createRoute(eventId))
                },
                onNavigateToDetail = { eventId ->
                    navController.navigate(Screen.Detail.createRoute(eventId))
                },
                onNavigateToCustomization = {
                    navController.navigate(Screen.WidgetCustomization.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToCalendar = {
                    navController.navigate(Screen.Calendar.route)
                }
            )
        }

        // Calendar Screen
        composable(Screen.Calendar.route) {
            CalendarScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToWidgets = {
                    navController.navigate(Screen.WidgetCustomization.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToAddEvent = {
                    navController.navigate(Screen.AddEvent.route)
                },
                onNavigateToEditEvent = { eventId ->
                    navController.navigate(Screen.EditEvent.createRoute(eventId))
                },
                onNavigateToDetail = { eventId ->
                    navController.navigate(Screen.Detail.createRoute(eventId))
                }
            )
        }

        // Add Event Screen
        composable(Screen.AddEvent.route) {
            val addEditViewModel: AddEditEventViewModel = viewModel()
            AddEditEventScreen(
                viewModel = addEditViewModel,
                eventId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Edit Event Screen
        composable(
            route = Screen.EditEvent.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getLong("eventId")
            val addEditViewModel: AddEditEventViewModel = viewModel()
            AddEditEventScreen(
                viewModel = addEditViewModel,
                eventId = eventId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Event Detail Screen
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getLong("eventId") ?: 0L
            val detailViewModel: EventDetailViewModel = viewModel()
            EventDetailScreen(
                viewModel = detailViewModel,
                eventId = eventId,
                onNavigateBack = {
                    val popped = navController.popBackStack()
                    if (!popped) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.EditEvent.createRoute(id))
                }
            )
        }

        // Widget Customization Screen
        composable(Screen.WidgetCustomization.route) {
            val customizationViewModel: CustomizationViewModel = viewModel()
            WidgetCustomizationScreen(
                viewModel = customizationViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToCalendar = {
                    navController.navigate(Screen.Calendar.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // Settings Screen
        composable(Screen.Settings.route) {
            val settingsViewModel: SettingsViewModel = viewModel()
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToCalendar = {
                    navController.navigate(Screen.Calendar.route)
                },
                onNavigateToWidgets = {
                    navController.navigate(Screen.WidgetCustomization.route)
                }
            )
        }
    }
}
