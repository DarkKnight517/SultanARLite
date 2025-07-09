package com.example.sultanarlite

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberAltairController(context: Context): AltairUIController {
    return remember { AltairUIController(context) }
}
