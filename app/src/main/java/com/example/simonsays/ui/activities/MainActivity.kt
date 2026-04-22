package com.example.simonsays.ui.activities

import android.os.Bundle
import android.util.TypedValue
import com.example.simonsays.R
import com.example.simonsays.model.SimonColor
import com.example.simonsays.ui.components.ButtonView
import com.example.simonsays.ui.components.SequenceView

class MainActivity : BaseActivity() {

    private lateinit var sequenceView: SequenceView
    private lateinit var gameButtons: List<ButtonView>

    // start of the Activity (start of UI)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null && !intent.hasExtra("is_restarting")) {
            gameManager.clearSequence()
        }
        
        setContentView(R.layout.activity_main)

        sequenceView = findViewById(R.id.sequenceView)

        setupButtons()
        setupControlButtons()
        setupMenuButtons()
        
        // load last saved sequence
        val saved = gameManager.loadSavedSequence()
        saved.forEach { sequenceView.addElement(it.first, it.second) }
    }

    // setup of all buttons (colored)
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

    // setup for delete and end game buttons
    private fun setupControlButtons() {
        val btnDelete = findViewById<ButtonView>(R.id.btnDeleteView)
        val btnEndGame = findViewById<ButtonView>(R.id.btnEndGameView)

        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, typedValue, true)
        val controlColor = typedValue.data

        btnDelete.setConfig(controlColor, getString(R.string.del), alpha = 0.2f, textSize = 50f, isBold = false)
        btnEndGame.setConfig(controlColor, getString(R.string.end), alpha = 0.2f, textSize = 50f, isBold = false)

        // DELETE
        btnDelete.setOnClickListener {
            if (sequenceView.getSequenceData().isNotEmpty()) {
                sequenceView.clear()
                gameManager.clearSequence()
            }
        }

        // END GAME
        btnEndGame.setOnClickListener {
            val data = sequenceView.getSequenceData()
            if (data.isNotEmpty()) {
                gameManager.addSequence(data)
                sequenceView.clear()
                gameManager.clearSequence()

                // all buttons glow as signal
                gameButtons.forEach { it.glow(300) }
            }
        }
    }

    // instance and sequence draft saver
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        gameManager.saveDraft(sequenceView.getSequenceData())
    }

    // helper function for language changes
    @Suppress("DEPRECATION")
    override fun onLanguageChanged() {
        gameManager.isEnglishLanguage = !gameManager.isEnglishLanguage
        gameManager.saveDraft(sequenceView.getSequenceData())
        recreate()
    }

    // helper function for colorblind mode changes
    override fun onColorblindModeChanged(enabled: Boolean) {
        gameButtons.forEach { it.setShowLabel(enabled) }
    }

    // sequence saver when theme is changed
    override fun onBeforeThemeChanged() {
        gameManager.saveDraft(sequenceView.getSequenceData())
    }
}
