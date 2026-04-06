package com.saico.ada.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.saico.ada.ui.navigation.routes.onboarding.OnboardingRoute
import com.saico.ada.onboarding.OnboardingScreen
import com.saico.ada.ui.navigation.routes.dashboard.DashboardRoute

fun NavGraphBuilder.onboardingGraph(navController: NavHostController) {
    navigation(
        startDestination = OnboardingRoute.OnboardingScreenRoute.route,
        route = OnboardingRoute.RootRoute.route
    ) {
        composable(route = OnboardingRoute.OnboardingScreenRoute.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(DashboardRoute.RootRoute.route) {
                        popUpTo(OnboardingRoute.RootRoute.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
