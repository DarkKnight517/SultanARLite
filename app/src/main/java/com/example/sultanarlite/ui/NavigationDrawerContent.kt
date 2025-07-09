package com.example.sultanarilite.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Info
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun AppDrawer(
    selectedScreen: String,
    onScreenSelected: (String) -> Unit,
    drawerState: DrawerState,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val screens = listOf(
        DrawerScreen("Главная", Icons.Default.Home),
        DrawerScreen("Профиль", Icons.Default.Person),
        DrawerScreen("Инфо", Icons.Default.Info),
        DrawerScreen("Меню", Icons.Default.Menu),
        DrawerScreen("Настройки", Icons.Default.Settings)
    )

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                screens.forEach { screen ->
                    NavigationDrawerItem(
                        label = { Text(screen.title) },
                        selected = selectedScreen == screen.title,
                        onClick = {
                            onScreenSelected(screen.title)
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        modifier = Modifier
                    )
                }
            }
        },
        drawerState = drawerState,
        content = content
    )
}

data class DrawerScreen(val title: String, val icon: ImageVector)
