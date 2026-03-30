package com.saico.ada.dashboard.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.saico.ada.dashboard.DashboardScreen
import com.saico.ada.ui.navigation.routes.dashboard.DashboardRoute

fun NavGraphBuilder.dashboardGraph(navController: NavHostController) {
    navigation(
        startDestination = DashboardRoute.DashboardScreenRoute.route,
        route = DashboardRoute.RootRoute.route
    ){
        composable(route = DashboardRoute.DashboardScreenRoute.route){
            DashboardScreen()
        }
    }
}