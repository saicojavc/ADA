package com.saico.ada.ui.navigation.routes.onboarding

import com.saico.ada.ui.navigation.routes.Route

interface OnboardingRoute : Route {

    data object RootRoute : OnboardingRoute {
        override val analyticsTag: String = "onboarding-flow"
        override val route: String = "onboarding"
    }

    data object OnboardingScreenRoute : OnboardingRoute {
        override val analyticsTag: String = "onboarding-screen-flow"
        override val route: String = "onboarding/onboarding-screen"
    }

}
