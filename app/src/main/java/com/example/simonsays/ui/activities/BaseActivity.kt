package com.example.simonsays.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageView

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

import com.example.simonsays.R
import com.example.simonsays.logic.GameManager

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var gameManager: GameManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameManager = GameManager(this)
        
        // saved theme
        if (gameManager.isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    protected fun setupMenuButtons() {
        findViewById<ImageView>(R.id.btnTheme)?.let { btn ->
            updateThemeIcon(btn)
            btn.setOnClickListener {
                gameManager.isDarkMode = !gameManager.isDarkMode
                if (gameManager.isDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                updateThemeIcon(btn)
            }
        }

        findViewById<ImageView>(R.id.btnColorblind)?.let { btn ->
            updateColorblindIcon(btn)
            btn.setOnClickListener {
                gameManager.isColorblindMode = !gameManager.isColorblindMode
                updateColorblindIcon(btn)
                onColorblindModeChanged(gameManager.isColorblindMode)
            }
        }

        findViewById<ImageView>(R.id.btnRecords)?.setOnClickListener {
            if (this !is RecordsActivity) {
                startActivity(Intent(this, RecordsActivity::class.java))
            } else if (this is RecordsActivity) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        findViewById<ImageView>(R.id.btnSettings)?.setOnClickListener {
            if (this !is SettingsActivity) {
                startActivity(Intent(this, SettingsActivity::class.java))
            } else if (this is SettingsActivity) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    private fun updateThemeIcon(btn: ImageView) {
        if (gameManager.isDarkMode) {
            btn.setImageResource(R.drawable.darkmood_icon)
        } else {
            btn.setImageResource(R.drawable.lightmood_icon)
        }
    }

    private fun updateColorblindIcon(btn: ImageView) {
        if (gameManager.isColorblindMode) {
            btn.setImageResource(R.drawable.hide_icon)
        } else {
            btn.setImageResource(R.drawable.view_icon)
        }
    }

    protected open fun onColorblindModeChanged(enabled: Boolean) {
        // TODO
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
}
