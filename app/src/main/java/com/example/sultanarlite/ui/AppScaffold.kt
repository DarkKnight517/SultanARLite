package com.example.sultanarlite.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sultanarlite.*
import com.example.sultanarlite.scenario.AltairScenarioManager
import com.example.sultanarlite.ui.altair.AltairDevSpaceScreen
import com.example.sultanarlite.ui.evolution.AltairEvolutionScreen
import com.example.sultanarlite.DialogScreen
import com.example.sultanarlite.MemoryScreen
import com.example.sultanarlite.VoiceScreen
import com.example.sultanarlite.HistoryScreen
import com.example.sultanarlite.ui.SettingsScreen

@Composable
fun AppScaffold(
    viewModel: MainViewModel,
    altairController: AltairUIController,
    snackbarHostState: SnackbarHostState
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val context = LocalContext.current
    val scenarioManager = remember { AltairScenarioManager(context) }
    val uiState by altairController.uiState.collectAsState()
    val showWelcome by viewModel.showWelcome.collectAsState()

    val panel = remember {
        AltairStatePanel(context).apply {
            updateCharacter(75, 60, 85)
        }
    }

    LaunchedEffect(uiState) {
        panel.updateFromState(uiState)
    }

    if (showWelcome) {
        WelcomeScreen(onStartClicked = { viewModel.dismissWelcome() })
        return
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                Column {
                    AndroidView(factory = { panel })
                    DialogScreen(viewModel = viewModel, altairController = altairController)
                }
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
            composable(Screen.Evolution.route) {
                AltairEvolutionScreen(scenarioManager = scenarioManager)
            }
            composable(Screen.DevSpace.route) {
                AltairDevSpaceScreen(altairController)
            }
        }
    }
}
