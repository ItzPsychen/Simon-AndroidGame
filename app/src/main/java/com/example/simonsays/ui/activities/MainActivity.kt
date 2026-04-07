package com.example.simonsays.ui.activities

import com.example.simonsays.R
import com.example.simonsays.model.SimonColor
import com.example.simonsays.ui.components.ButtonView
import com.example.simonsays.ui.components.SequenceView

import android.os.Bundle
import android.graphics.Color

class MainActivity : BaseActivity() {

    private lateinit var sequenceView: SequenceView
    private lateinit var gameButtons: List<ButtonView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sequenceView = findViewById(R.id.sequenceView)

        setupButtons()
        setupControlButtons()
        setupMenuButtons()

        // load saved sequence
        val saved = gameManager.loadSavedSequence()
        saved.forEach { sequenceView.addElement(it.first, it.second) }
    }

    private fun setupButtons() {
        val colors = SimonColor.entries.toTypedArray()
        gameButtons = listOf(
            findViewById(R.id.btn0),
            findViewById(R.id.btn1),
            findViewById(R.id.btn2),
            findViewById(R.id.btn3),
            findViewById(R.id.btn4),
            findViewById(R.id.btn5)
        )

        gameButtons.forEachIndexed { index, button ->
            if (index < colors.size) {
                val colorData = colors[index]
                button.setConfig(colorData.colorRes, colorData.label)
                button.setShowLabel(gameManager.isColorblindMode)
                
                button.setOnClickListener {
                    sequenceView.addElement(colorData.label, colorData.colorRes)
                }
            }
        }
    }

    private fun setupControlButtons() {
        val btnDelete = findViewById<ButtonView>(R.id.btnDeleteView)
        val btnEndGame = findViewById<ButtonView>(R.id.btnEndGameView)

        val controlColor = Color.BLACK
        btnDelete.setConfig(controlColor, "DELETE", alpha = 0.2f, textSize = 50f, isBold = false)
        btnEndGame.setConfig(controlColor, "END GAME", alpha = 0.2f, textSize = 50f, isBold = false)

        btnDelete.setOnClickListener {
            sequenceView.clear()
            gameManager.clearSequence()
        }

        btnEndGame.setOnClickListener {
            gameManager.saveSequence(sequenceView.getSequenceData())
        }
    }

    override fun onColorblindModeChanged(enabled: Boolean) {
        gameButtons.forEach { it.setShowLabel(enabled) }
    }
}
