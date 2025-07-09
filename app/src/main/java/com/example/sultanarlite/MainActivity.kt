package com.example.sultanarlite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sultanarlite.ui.theme.SultanARLiteTheme
import com.example.sultanarlite.ui.AppScaffold

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SultanARLiteTheme {
                val viewModel: MainViewModel = viewModel()
                val altairController = rememberAltairController(LocalContext.current)
                AppScaffold(
                    viewModel = viewModel,
                    altairController = altairController
                )
            }
        }
    }
}
