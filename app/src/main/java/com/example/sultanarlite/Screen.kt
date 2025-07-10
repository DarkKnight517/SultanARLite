package com.example.sultanarlite

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Определение всех экранов навигации
sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Dialog    : Screen("dialog",    "Диалог",       Icons.Filled.Chat)
    object Memory    : Screen("memory",    "Память",       Icons.Filled.Memory)
    object Voice     : Screen("voice",     "Голос",        Icons.Filled.Mic)
    object History   : Screen("history",   "История",      Icons.Filled.History)
    object Settings  : Screen("settings",  "Настройки",    Icons.Filled.Settings)
    object Evolution : Screen("evolution", "Развитие",     Icons.Filled.AutoAwesome)
    object DevSpace  : Screen("dev_space", "Лаборатория",  Icons.Filled.Build)

    companion object {
        val all: List<Screen> = listOf(
            Dialog, Memory, Voice, History, Settings, Evolution, DevSpace
        )

    }
}




// Экран приветствия (Welcome)
@Composable
fun WelcomeScreen(onStartClicked: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Добро пожаловать в Sultan AR Lite",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Голосовой ИИ-помощник с памятью, интеллектом и визуализацией.\n\nГотов к работе.",
                fontSize = 16.sp,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onStartClicked,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Начать")
            }
        }
    }
}
