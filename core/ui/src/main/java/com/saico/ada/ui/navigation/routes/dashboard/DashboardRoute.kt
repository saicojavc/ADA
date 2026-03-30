package com.saico.ada.ui.navigation.routes.dashboard

import com.saico.ada.ui.navigation.routes.Route

interface DashboardRoute : Route {

    data object RootRoute : DashboardRoute {
        override val analyticsTag: String = "dashboard-flow"
        override val route: String = "dashboard"
    }

    data object DashboardScreenRoute : DashboardRoute {
        override val analyticsTag: String = "dashboard-screen-flow"
        override val route: String = "dashboard/dashboard-screen"
    }

}