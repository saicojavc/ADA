package com.saico.ada

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.saico.ada.dashboard.navigation.dashboardGraph
import com.saico.ada.ui.navigation.Navigator
import com.saico.ada.ui.theme.ADATheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigator: Navigator

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ADATheme {
                val navController = rememberNavController()


                Surface(modifier = Modifier.fillMaxSize())  {
                    MainContainer(
                        startDestination = viewModel.firstScreen,
                        navigator = navigator,
                        navController = navController
                    )
                }
                }
            }
        }
    }
@Composable
private fun MainContainer(
    startDestination: String,
    navigator: Navigator,
    navController: NavHostController,
){
    Column {
        NavHost(
            navController = navController,
            startDestination = startDestination,
        ){
            dashboardGraph(navController = navController)
        }
    }
}
