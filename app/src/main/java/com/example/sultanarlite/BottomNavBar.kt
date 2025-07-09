package com.example.sultanarlite

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy

@Composable
fun BottomNavBar(
    navController: NavController,
    currentDestination: NavDestination?
) {
    println("DEBUG Screen.all: ${Screen.all}")
    Screen.all.forEachIndexed { i, screen ->
        println("DEBUG screen[$i]: $screen, class=${screen?.javaClass}")
    }
    NavigationBar {
        Screen.all.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            NavigationBarItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}
