package com.example.sultanarlite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _selectedScreen = MutableStateFlow<Screen>(Screen.Dialog)
    val selectedScreen: StateFlow<Screen> = _selectedScreen.asStateFlow()

    private val _showWelcome = MutableStateFlow(true)
    val showWelcome: StateFlow<Boolean> = _showWelcome.asStateFlow()

    fun selectScreen(screen: Screen) {
        _selectedScreen.value = screen
    }

    fun dismissWelcome() {
        _showWelcome.value = false
    }
}
