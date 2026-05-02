package com.example.simonsays.ui.activities

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView

import com.example.simonsays.R

class SettingsActivity : BaseActivity() {

    private lateinit var sbIntensity: SeekBar
    private lateinit var tvColorblind: TextView
    private lateinit var tvLanguage: TextView
    private lateinit var tvTheme: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupMenuButtons()

        // initialize UI components
        sbIntensity = findViewById(R.id.sbSoundIntensity)
        tvColorblind = findViewById(R.id.tvColorblindValue)
        tvLanguage = findViewById(R.id.tvLanguageValue)
        tvTheme = findViewById(R.id.tvThemeValue)

        // buttons for each functionality
        val btnToggleColorblind = findViewById<LinearLayout>(R.id.btnToggleColorblind)
        val btnToggleLanguage = findViewById<LinearLayout>(R.id.btnToggleLanguage)
        val btnToggleTheme = findViewById<LinearLayout>(R.id.btnToggleTheme)
        val btnReset = findViewById<Button>(R.id.btnResetDefault)
        val btnDelete = findViewById<Button>(R.id.btnDeleteAllRecords)

        // loads the current settings (saved ones)
        updateUI()

        // sound effects seekbar listener
        sbIntensity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    gameManager.soundVolume = progress
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // colorblind enabler listener
        btnToggleColorblind.setOnClickListener {
            gameManager.isColorblindMode = !gameManager.isColorblindMode
            onColorblindModeChanged(gameManager.isColorblindMode)
            updateUI()
        }

        // language changer listener
        btnToggleLanguage.setOnClickListener {
            gameManager.isEnglishLanguage = !gameManager.isEnglishLanguage
            onLanguageChanged()
        }

        // theme changer listener
        btnToggleTheme.setOnClickListener {
            gameManager.isDarkMode = !gameManager.isDarkMode
            recreate()
        }

        // RESET DEFAULT
        btnReset.setOnClickListener {
            gameManager.resetToDefault()
            recreate()
        }

        // DELETE ALL
        btnDelete.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    // updates UI whenever the settings change
    private fun updateUI() {
        sbIntensity.progress = gameManager.soundVolume
        
        tvColorblind.text = if (gameManager.isColorblindMode) getString(R.string.on) else getString(R.string.off)
        tvLanguage.text = if (gameManager.isEnglishLanguage) "EN" else "IT"
        tvTheme.text = if (gameManager.isDarkMode) getString(R.string.dark_mode).uppercase() else getString(R.string.light_mode).uppercase()
    }

    // delete all confirmation pop-up
    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_records)
            .setMessage(R.string.confirm_delete)
            .setPositiveButton(R.string.yes) { _, _ ->
                gameManager.clearHistory()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
}
