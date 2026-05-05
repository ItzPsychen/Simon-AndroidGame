package com.example.simonsays.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView

import com.example.simonsays.R

class SettingsActivity : BaseActivity() {

    private lateinit var sbIntensity: SeekBar
    private lateinit var sbGameSpeed: SeekBar
    private lateinit var tvVolumeValue: TextView
    private lateinit var tvSpeedValue: TextView
    private lateinit var tvColorblind: TextView
    private lateinit var tvLanguage: TextView
    private lateinit var tvTheme: TextView
    
    private lateinit var btnReset: Button
    private lateinit var btnDelete: Button
    private var isConfirmingDelete = false

    private val speedValues = listOf(0.25f, 0.5f, 1.0f, 2.0f, 4.0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupMenuButtons()

        // initialize UI components
        sbIntensity = findViewById(R.id.sbSoundIntensity)
        sbGameSpeed = findViewById(R.id.sbGameSpeed)
        tvVolumeValue = findViewById(R.id.tvVolumeValue)
        tvSpeedValue = findViewById(R.id.tvSpeedValue)
        tvColorblind = findViewById(R.id.tvColorblindValue)
        tvLanguage = findViewById(R.id.tvLanguageValue)
        tvTheme = findViewById(R.id.tvThemeValue)

        // buttons for each functionality
        val btnToggleColorblind = findViewById<LinearLayout>(R.id.btnToggleColorblind)
        val btnToggleLanguage = findViewById<LinearLayout>(R.id.btnToggleLanguage)
        val btnToggleTheme = findViewById<LinearLayout>(R.id.btnToggleTheme)
        btnReset = findViewById(R.id.btnResetDefault)
        btnDelete = findViewById(R.id.btnDeleteAllRecords)

        // restore state if recreated
        if (savedInstanceState != null) {
            isConfirmingDelete = savedInstanceState.getBoolean("isConfirmingDelete", false)
        }

        // loads the current settings (saved ones)
        updateUI()

        // ensure buttons have correct text based on state
        toggleDeleteConfirmation(isConfirmingDelete)

        // SOUND EFFECTS seekbar listener
        sbIntensity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvVolumeValue.text = progress.toString()
                if (fromUser) {
                    gameManager.soundVolume = progress
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // GAME SPEED seekbar listener
        sbGameSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val speed = speedValues.getOrElse(progress) { 1.0f }
                tvSpeedValue.text = "x$speed"
                if (fromUser) {
                    gameManager.gameSpeed = speed
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // COLORBLIND listener
        btnToggleColorblind.setOnClickListener {
            gameManager.isColorblindMode = !gameManager.isColorblindMode
            onColorblindModeChanged(gameManager.isColorblindMode)
            updateUI()
        }

        // LANGUAGE listener
        btnToggleLanguage.setOnClickListener {
            gameManager.isEnglishLanguage = !gameManager.isEnglishLanguage
            onLanguageChanged()
        }

        // THEME listener
        btnToggleTheme.setOnClickListener {
            gameManager.isDarkMode = !gameManager.isDarkMode
            recreate()
        }

        // RESET DEFAULT / CANCEL
        btnReset.setOnClickListener {
            if (isConfirmingDelete) {
                toggleDeleteConfirmation(false)
            } else {
                gameManager.resetToDefault()
                // update UI immediately
                updateUI()
                onLanguageChanged()
            }
        }

        // DELETE ALL / CONFIRM
        btnDelete.setOnClickListener {
            if (isConfirmingDelete) {
                gameManager.clearHistory()
                toggleDeleteConfirmation(false)
            } else {
                toggleDeleteConfirmation(true)
            }
        }
    }

    // save state on configuration change
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isConfirmingDelete", isConfirmingDelete)
    }

    // updates UI whenever the settings change
    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        sbIntensity.progress = gameManager.soundVolume
        tvVolumeValue.text = gameManager.soundVolume.toString()
        
        val currentSpeed = gameManager.gameSpeed
        val speedIndex = speedValues.indexOf(currentSpeed).coerceAtLeast(0)
        sbGameSpeed.progress = speedIndex
        tvSpeedValue.text = "x$currentSpeed"
        
        tvColorblind.text = if (gameManager.isColorblindMode) getString(R.string.on) else getString(R.string.off)
        tvLanguage.text = if (gameManager.isEnglishLanguage) "EN" else "IT"
        tvTheme.text = if (gameManager.isDarkMode) getString(R.string.dark_mode).uppercase() else getString(R.string.light_mode).uppercase()
    }

    // changes between normal state and delete confirmation state
    private fun toggleDeleteConfirmation(active: Boolean) {
        isConfirmingDelete = active
        if (active) {
            btnReset.setText(R.string.cancel)
            btnDelete.setText(R.string.confirm)
        } else {
            btnReset.setText(R.string.reset_default)
            btnDelete.setText(R.string.delete_records)
        }
    }
}
