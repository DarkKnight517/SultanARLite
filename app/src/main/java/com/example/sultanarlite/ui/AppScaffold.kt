package com.example.sultanarlite.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sultanarlite.*
import com.example.sultanarlite.ui.evolution.AltairEvolutionScreen
import com.example.sultanarlite.scenario.AltairScenarioManager

@Composable
fun AppScaffold(
    viewModel: MainViewModel,
    altairController: AltairUIController
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val context = LocalContext.current
    val scenarioManager = remember { AltairScenarioManager(context) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentDestination = currentDestination
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dialog.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dialog.route) {
                DialogScreen(
                    viewModel = viewModel,
                    altairController = altairController
                )
            }
            composable(Screen.Memory.route) {
                MemoryScreen(controller = altairController)
            }
            composable(Screen.Voice.route) {
                VoiceScreen(viewModel = viewModel, altairController = altairController)
            }
            composable(Screen.History.route) {
                HistoryScreen(controller = altairController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            composable(Screen.Lab.route) {
                AltairLabScreen(controller = altairController)
            }
            composable(Screen.Evolution.route) {
                AltairEvolutionScreen(scenarioManager = scenarioManager)
            }
        }
    }
}
