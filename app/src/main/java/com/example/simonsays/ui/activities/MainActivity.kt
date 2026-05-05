package com.example.simonsays.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.Toast

import com.example.simonsays.R
import com.example.simonsays.model.SimonColor
import com.example.simonsays.ui.components.ButtonView
import com.example.simonsays.ui.components.SequenceView
import com.example.simonsays.logic.TonePlayer.ToneConstants
import com.example.simonsays.logic.GameManager

import kotlin.random.Random

class MainActivity : BaseActivity() {

    private lateinit var sequenceView: SequenceView
    private lateinit var gameButtons: List<ButtonView>
    
    private val gameSequence = mutableListOf<SimonColor>()
    private var playerIndex = 0
    private var sequenceIndexToShow = 0
    private var isPlayingSequence = false
    private var isGameRunning = false
    private var isPaused = false
    
    private val handler = Handler(Looper.getMainLooper())
    
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
        
        // restore from draft
        val saved = gameManager.loadSavedSequence()
        this.isGameRunning = saved.isRunning
        this.playerIndex = saved.playerIndex
        
        gameSequence.clear()
        saved.targetSequenceLabels.forEach { label ->
            SimonColor.entries.find { it.label == label }?.let { gameSequence.add(it) }
        }

        saved.sequence.forEach { 
            val color = if (it.isError) Color.RED else it.color
            sequenceView.addElement(it.label, color)
        }

        if (isGameRunning) {
            findViewById<View>(R.id.containerStart).visibility = View.GONE
            findViewById<View>(R.id.containerPause).visibility = View.VISIBLE
            findViewById<View>(R.id.containerEndGame).visibility = View.VISIBLE
        } else {
            sequenceView.clear()
        }
    }

    // called when resuming the activity
    override fun onResume() {
        super.onResume()
        if (!isGameRunning) {
            sequenceView.clear()
        }
    }

    // setup for colored buttons and listeners
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
                
                if (index < ToneConstants.COLOR_FREQUENCIES.size) {
                    button.setFrequency(ToneConstants.COLOR_FREQUENCIES[index])
                }
                
                button.setOnClickListener {
                    if (isGameRunning && !isPlayingSequence && !isPaused) {
                        handlePlayerInput(colorData)
                    }
                }
            }
        }
    }

    // setup for control buttons and listeners
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

        btnStart.setSoundEnabled(false)
        btnPause.setSoundEnabled(false)
        btnEndGame.setSoundEnabled(false)

        // START GAME
        btnStart.setOnClickListener {
            startGame()
            containerStart.visibility = View.GONE
            containerPause.visibility = View.VISIBLE
            containerEndGame.visibility = View.VISIBLE
        }

        // PAUSE
        btnPause.setOnClickListener {
            if (!isGameRunning) return@setOnClickListener
            isPaused = !isPaused
            btnPause.setConfig(controlColor, if (isPaused) getString(R.string.resume) else getString(R.string.pause), alpha = 0.2f, textSize = 50f, isBold = false)
            if (!isPaused && isPlayingSequence) {
                showSequence()
            }
        }

        // END GAME
        btnEndGame.setOnClickListener {
            gameOver(hasMistake = false)
        }
    }

    // start of the game
    private fun startGame() {
        isGameRunning = true
        isPaused = false
        gameSequence.clear()
        sequenceView.clear()
        nextRound()
    }

    // makes the next round of the game
    private fun nextRound() {
        playerIndex = 0
        sequenceIndexToShow = 0
        val colors = SimonColor.entries.toTypedArray()
        gameSequence.add(colors[Random.nextInt(colors.size)])
        showSequence()
    }

    // enables/disables all buttons
    private fun setButtonsEnabled(enabled: Boolean) {
        gameButtons.forEach { it.isEnabled = enabled }
    }

    // shows the sequence to be replicated
    private fun showSequence() {
        if (!isGameRunning || isPaused) return
        isPlayingSequence = true
        setButtonsEnabled(false)
        
        // default delay is 600ms
        val delay = (600 / gameManager.gameSpeed).toLong()
        
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            if (!isGameRunning || isPaused) {
                setButtonsEnabled(true)
                return@postDelayed
            }
            
            val color = gameSequence.getOrNull(sequenceIndexToShow) ?: return@postDelayed
            val buttonIndex = SimonColor.entries.indexOf(color)
            if (buttonIndex in gameButtons.indices) {
                val button = gameButtons[buttonIndex]

                // glow duration based on speed
                val glowDuration = 800L.coerceAtMost((delay / 2))
                button.glow(glowDuration)
                button.playSound()
            }
            
            sequenceIndexToShow++
            if (sequenceIndexToShow < gameSequence.size) {
                showSequence()
            } else {
                isPlayingSequence = false
                sequenceIndexToShow = 0 
                setButtonsEnabled(true)
            }
        }, delay)
    }

    // handler for player input
    private fun handlePlayerInput(color: SimonColor) {
        if (color == gameSequence[playerIndex]) {
            sequenceView.addElement(color.label, color.colorRes)
            playerIndex++
            if (playerIndex == gameSequence.size) {
                isPlayingSequence = true
                
                // glow all buttons
                gameButtons.forEach { it.glow(300) }
                
                handler.postDelayed({
                    if (!isGameRunning) return@postDelayed
                    sequenceView.clear(true)
                    nextRound()
                }, 0)
            }
        } else {
            gameOver(hasMistake = true)
        }
    }

    // game over called on the first mistake or with END GAME
    private fun gameOver(hasMistake: Boolean) {
        if (!isGameRunning) return
        isGameRunning = false
        handler.removeCallbacksAndMessages(null)
        setButtonsEnabled(true)
        
        val score = gameSequence.size - 1
        val historySequence = mutableListOf<GameManager.SequenceElement>()
        sequenceView.clear()

        // case of mistake or just ending the game
        if (!hasMistake) {
            for (i in 0 until score) {
                val c = gameSequence[i]
                historySequence.add(GameManager.SequenceElement(c.label, c.colorRes, false))
            }
        } else {
            val displayedLength = if (playerIndex == score) score + 1 else score
            for (i in 0 until displayedLength) {
                val c = gameSequence[i]
                val isError = (i == playerIndex)
                historySequence.add(GameManager.SequenceElement(c.label, c.colorRes, isError))
            }
        }
        
        if (historySequence.isNotEmpty() || score > 0) {
            gameManager.addSequence(score, historySequence)
        }
        
        sequenceView.clear()
        gameManager.clearSequence() 
        
        findViewById<View>(R.id.containerStart).visibility = View.VISIBLE
        findViewById<View>(R.id.containerPause).visibility = View.GONE
        findViewById<View>(R.id.containerEndGame).visibility = View.GONE
        
        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, typedValue, true)
        findViewById<ButtonView>(R.id.btnPauseView).setConfig(
            typedValue.data, getString(R.string.pause), alpha = 0.2f, textSize = 50f, isBold = false
        )
        isPaused = false

        // only if score is at least 1
        if (score > 0) {
            Toast.makeText(this, "Game Over! Score: $score", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RecordsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
        }
    }

    // save the game as draft
    private fun saveCurrentStateAsDraft() {
        val score = if (isGameRunning) gameSequence.size - 1 else 0
        val targetLabels = gameSequence.map { it.label }
        val sequenceData = sequenceView.getSequenceData().map { (label, color) ->
            GameManager.SequenceElement(label, color, color == Color.RED)
        }
        gameManager.saveDraft(score, playerIndex, isGameRunning, targetLabels, sequenceData)
    }

    // save draft on configuration change
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveCurrentStateAsDraft()
    }

    // save draft on language change
    override fun onLanguageChanged() {
        saveCurrentStateAsDraft()
        super.onLanguageChanged()
    }

    // save draft on colorblind mode change
    override fun onColorblindModeChanged(enabled: Boolean) {
        saveCurrentStateAsDraft()
        super.onColorblindModeChanged(enabled)
    }

    // save draft on configuration change
    override fun onBeforeConfigChanged() {
        saveCurrentStateAsDraft()
    }
}
