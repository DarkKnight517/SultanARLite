package com.example.sultanarlite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sultanarlite.ui.AppScaffold
import com.example.sultanarlite.ui.theme.SultanARLiteTheme
import com.example.sultanarlite.WelcomeScreen
import com.sultan.altair.update.AltairUpdater // ✅ импорт обновлений
import com.sultan.altair.trainer.AltairAutoTrainer // ✅ импорт тренера

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Вызов обновления Альтаира при запуске
        AltairUpdater.checkAndUpdate()

        // ✅ Запуск автообучения после обновления
        AltairAutoTrainer.trainOnce()

        setContent {
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
                        altairController = altairController
                    )
                }
            }
        }
    }
}
