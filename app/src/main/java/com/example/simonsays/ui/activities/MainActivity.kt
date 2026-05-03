package com.example.simonsays.ui.activities

import android.os.Bundle
import android.util.TypedValue
import android.view.View

import com.example.simonsays.R
import com.example.simonsays.model.SimonColor
import com.example.simonsays.ui.components.ButtonView
import com.example.simonsays.ui.components.SequenceView
import com.example.simonsays.logic.ToneConstants

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
                
                // set specific frequency for each colored button
                if (index < ToneConstants.COLOR_FREQUENCIES.size) {
                    button.setFrequency(ToneConstants.COLOR_FREQUENCIES[index])
                }
                
                button.setOnClickListener {
                    sequenceView.addElement(colorData.label, colorData.colorRes)
                }
            }
        }
    }

    // setup for control buttons: START GAME, PAUSE, END GAME
    private fun setupControlButtons() {
        val btnStart = findViewById<ButtonView>(R.id.btnStartView)
        val btnPause = findViewById<ButtonView>(R.id.btnPauseView)
        val btnEndGame = findViewById<ButtonView>(R.id.btnEndGameView)

        val containerStart = findViewById<View>(R.id.containerStart)
        val containerPause = findViewById<View>(R.id.containerPause)
        val containerEndGame = findViewById<View>(R.id.containerEndGame)

        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, typedValue, true)
        val controlColor = typedValue.data

        btnStart.setConfig(controlColor, getString(R.string.start), alpha = 0.2f, textSize = 50f, isBold = false)
        btnPause.setConfig(controlColor, getString(R.string.pause), alpha = 0.2f, textSize = 50f, isBold = false)
        btnEndGame.setConfig(controlColor, getString(R.string.end), alpha = 0.2f, textSize = 50f, isBold = false)

        // disable sound for control buttons
        btnStart.setSoundEnabled(false)
        btnPause.setSoundEnabled(false)
        btnEndGame.setSoundEnabled(false)

        // START GAME: hide START, show PAUSE and END
        btnStart.setOnClickListener {
            containerStart.visibility = View.GONE
            containerPause.visibility = View.VISIBLE
            containerEndGame.visibility = View.VISIBLE
        }

        // PAUSE
        btnPause.setOnClickListener {
            // TODO
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

            containerStart.visibility = View.VISIBLE
            containerPause.visibility = View.GONE
            containerEndGame.visibility = View.GONE
        }
    }

    // instance and sequence draft saver
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        gameManager.saveDraft(sequenceView.getSequenceData())
    }

    // helper function for language changes
    override fun onLanguageChanged() {
        gameManager.saveDraft(sequenceView.getSequenceData())
        super.onLanguageChanged()
    }

    // helper function for colorblind mode changes
    override fun onColorblindModeChanged(enabled: Boolean) {
        gameManager.saveDraft(sequenceView.getSequenceData())
        super.onColorblindModeChanged(enabled)
    }

    // helper function for configuration changes
    override fun onBeforeConfigChanged() {
        gameManager.saveDraft(sequenceView.getSequenceData())
    }
}
