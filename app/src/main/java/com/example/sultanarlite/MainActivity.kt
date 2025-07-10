package com.example.sultanarlite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sultanarlite.ui.AppScaffold
import com.example.sultanarlite.ui.theme.SultanARLiteTheme
import com.example.sultanarlite.WelcomeScreen
import com.example.sultanarlite.altair.update.AltairUpdater
import com.example.sultanarlite.altair.trainer.AltairAutoTrainer
import com.example.sultanarlite.altair.core.AltairMemory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🧠 Инициализация памяти перед обновлением и обучением
        AltairMemory.init(applicationContext)

        lifecycleScope.launch(Dispatchers.IO) {
            AltairUpdater.checkAndUpdate(applicationContext)
            AltairAutoTrainer.trainOnce()
        }

        setContent {
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar("🧠 Альтаир обновлён и обучен")
            }

            SultanARLiteTheme {
                val viewModel: MainViewModel = viewModel()
                val altairController = rememberAltairController(LocalContext.current)
                val showWelcome = viewModel.showWelcome.collectAsState().value

                if (showWelcome) {
                    WelcomeScreen(onStartClicked = {
                        viewModel.dismissWelcome()
                    })
                } else {
                    AppScaffold(
                        viewModel = viewModel,
                        altairController = altairController,
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}
