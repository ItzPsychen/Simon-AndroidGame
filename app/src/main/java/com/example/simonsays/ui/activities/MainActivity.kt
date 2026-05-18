package com.example.simonsays.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope

import com.example.simonsays.R
import com.example.simonsays.logic.GameManager
import com.example.simonsays.logic.TonePlayer.ToneConstants
import com.example.simonsays.model.SimonColor
import com.example.simonsays.ui.components.ButtonView
import com.example.simonsays.ui.components.SequenceView
import kotlinx.coroutines.launch

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
            lifecycleScope.launch {
                gameManager.clearSequence()
            }
        }
        
        setContentView(R.layout.activity_main)

        sequenceView = findViewById(R.id.sequenceView)

        setupButtons()
        setupControlButtons()
        setupMenuButtons()
        
        // restore from draft
        lifecycleScope.launch {
            val saved = gameManager.loadSavedSequence()
            isGameRunning = saved.isRunning
            playerIndex = saved.playerIndex
            isPaused = saved.isPaused
            
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
                
                updateControlButtonsUI()
            }
        }
    }

    // update color of the control buttons
    private fun updateControlButtonsUI() {
        val btnPause = findViewById<ButtonView>(R.id.btnPauseView)
        val btnEndGame = findViewById<ButtonView>(R.id.btnEndGameView)
        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, typedValue, true)
        val controlColor = typedValue.data
        
        btnPause.setConfig(controlColor, if (isPaused) getString(R.string.resume) else getString(R.string.pause), alpha = 0.2f, textSize = 50f, isBold = false)
        
        btnEndGame.isEnabled = isPaused
        val endAlpha = if (isPaused) 0.2f else 0.1f
        btnEndGame.setConfig(controlColor, getString(R.string.end), alpha = endAlpha, textSize = 50f, isBold = false)
    }

    override fun onResume() {
        super.onResume()
        if (!isGameRunning) {
            sequenceView.clear()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isGameRunning && !isPaused) {
            isPaused = true
            updateControlButtonsUI()
            saveCurrentStateAsDraft()
        }
    }

    // sets up the buttons
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

    // sets up the control buttons
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
        
        updateControlButtonsUI()

        btnStart.setSoundEnabled(false)
        btnPause.setSoundEnabled(false)
        btnEndGame.setSoundEnabled(false)

        btnStart.setOnClickListener {
            startGame()
            containerStart.visibility = View.GONE
            containerPause.visibility = View.VISIBLE
            containerEndGame.visibility = View.VISIBLE
            updateControlButtonsUI()
        }

        btnPause.setOnClickListener {
            if (!isGameRunning) return@setOnClickListener
            isPaused = !isPaused
            updateControlButtonsUI()
            if (!isPaused && isPlayingSequence) {
                showSequence()
            }
        }

        btnEndGame.setOnClickListener {
            gameOver(hasMistake = false)
        }
    }

    // game starter
    private fun startGame() {
        isGameRunning = true
        isPaused = false
        gameSequence.clear()
        sequenceView.clear()
        nextRound()
    }

    // creates the next round
    private fun nextRound() {
        playerIndex = 0
        sequenceIndexToShow = 0
        val colors = SimonColor.entries.toTypedArray()
        if (gameManager.isRepetitionAllowed) {
            gameSequence.add(colors[Random.nextInt(colors.size)])
        } else {
            val lastColorIndex = gameSequence.lastOrNull()?.ordinal ?: -1
            var nextColorIndex: Int
            do {
                nextColorIndex = Random.nextInt(colors.size)
            } while (nextColorIndex == lastColorIndex)
            gameSequence.add(colors[nextColorIndex])
        }
        showSequence()
    }

    // enables/disables the buttons
    private fun setButtonsEnabled(enabled: Boolean) {
        gameButtons.forEach { it.isEnabled = enabled }
    }

    // shows the sequence in game
    private fun showSequence() {
        if (!isGameRunning || isPaused) return
        isPlayingSequence = true
        setButtonsEnabled(false)
        
        val delay = (600 / gameManager.gameSpeed).toLong()
        val currentDelay = if (sequenceIndexToShow == 0) 1000L.coerceAtLeast(delay) else delay

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
        }, currentDelay)
    }

    // gets the player input
    private fun handlePlayerInput(color: SimonColor) {
        if (color == gameSequence[playerIndex]) {
            sequenceView.addElement(color.label, color.colorRes)
            playerIndex++
            if (playerIndex == gameSequence.size) {
                isPlayingSequence = true
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

    // game end (mistake or manual)
    private fun gameOver(hasMistake: Boolean) {
        if (!isGameRunning) return
        isGameRunning = false
        handler.removeCallbacksAndMessages(null)
        setButtonsEnabled(true)
        
        val score = gameSequence.size - 1
        val historySequence = mutableListOf<GameManager.SequenceElement>()
        
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
        
        lifecycleScope.launch {
            if (score > 0) {
                gameManager.addSequence(score, historySequence, System.currentTimeMillis(), gameManager.isRepetitionAllowed)
            }
            gameManager.clearSequence() 
            
            sequenceView.clear()
            findViewById<View>(R.id.containerStart).visibility = View.VISIBLE
            findViewById<View>(R.id.containerPause).visibility = View.GONE
            findViewById<View>(R.id.containerEndGame).visibility = View.GONE
            
            isPaused = false
            updateControlButtonsUI()

            if (score > 0) {
                Toast.makeText(this@MainActivity, "Game Over! Score: $score", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity, RecordsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
            }
        }
    }

    // saves the state as draft
    private fun saveCurrentStateAsDraft() {
        val score = if (isGameRunning) gameSequence.size - 1 else 0
        val targetLabels = gameSequence.map { it.label }
        val sequenceData = sequenceView.getSequenceData().map { (label, color) ->
            GameManager.SequenceElement(label, color, color == Color.RED)
        }
        lifecycleScope.launch {
            gameManager.saveDraft(score, playerIndex, isGameRunning, targetLabels, sequenceData, isPaused)
        }
    }

    // save state on configuration change
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveCurrentStateAsDraft()
    }

    // save state on language change
    override fun onLanguageChanged() {
        saveCurrentStateAsDraft()
        super.onLanguageChanged()
    }

    // save state on colorblind mode change
    override fun onColorblindModeChanged(enabled: Boolean) {
        saveCurrentStateAsDraft()
        super.onColorblindModeChanged(enabled)
    }

    // save state on theme change
    override fun onBeforeConfigChanged() {
        saveCurrentStateAsDraft()
    }
}
