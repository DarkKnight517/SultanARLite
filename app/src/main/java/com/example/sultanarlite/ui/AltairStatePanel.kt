package com.example.sultanarlite.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.example.sultanarlite.R
import com.example.sultanarlite.AltairUIState
import com.sultanarlite.core.AltairLearningMemory

class AltairStatePanel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val confidenceText: TextView
    private val logicText: TextView
    private val empathyText: TextView
    private val memoryText: TextView

    private val goalText: TextView
    private val internetText: TextView
    private val resultText: TextView

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_altair_state_panel, this, true)

        confidenceText = findViewById(R.id.textConfidence)
        logicText = findViewById(R.id.textLogic)
        empathyText = findViewById(R.id.textEmpathy)
        memoryText = findViewById(R.id.textMemory)

        goalText = TextView(context).apply {
            textSize = 16f
            setPadding(0, 12, 0, 4)
        }
        internetText = TextView(context).apply {
            textSize = 16f
            setPadding(0, 4, 0, 4)
        }
        resultText = TextView(context).apply {
            textSize = 16f
            setPadding(0, 4, 0, 12)
        }

        addView(goalText)
        addView(internetText)
        addView(resultText)
    }

    fun updateCharacter(confidence: Int, logic: Int, empathy: Int) {
        confidenceText.text = "Уверенность: $confidence%"
        logicText.text = "Логика: $logic%"
        empathyText.text = "Эмпатия: $empathy%"
    }

    fun updateMemory(memory: AltairLearningMemory) {
        val top = memory.getMostUsedCommands(5).joinToString("\n") { "- $it" }
        memoryText.text = "Часто используемые команды:\n$top"
    }

    fun setGoal(goal: String) {
        goalText.text = "🎯 Цель: $goal"
    }

    fun setInternet(enabled: Boolean) {
        internetText.text = if (enabled) "📡 Интернет: ВКЛ" else "📴 Интернет: ВЫКЛ"
    }

    fun setLastResult(result: String) {
        resultText.text = "📊 Последний результат: $result"
    }

    fun updateFromState(state: AltairUIState) {
        setGoal(state.currentGoal)
        setInternet(state.internetEnabled)
        setLastResult(state.lastInternetResult)
    }
}
