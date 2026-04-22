package com.example.simonsays.ui.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageView

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

import com.example.simonsays.R
import com.example.simonsays.logic.GameManager

// only to force english language as default
import java.util.Locale

abstract class BaseActivity : AppCompatActivity() {

    // lateinit for non-nullable and not initialized
    protected lateinit var gameManager: GameManager

    // can be accessed without an instance of class
    companion object {
        private var lastClickTime: Long = 0
        private const val DEBOUNCE_TIME = 300L
        private var isChangingTheme: Boolean = false
    }

    // called before the onCreate (before the creation of the UI)
    @Suppress("DEPRECATION")
    override fun attachBaseContext(newBase: Context) {
        // getting info from SharedPreferences
        val sharedPref = newBase.getSharedPreferences("SimonSaysPrefs", MODE_PRIVATE)
        val isEnglish = sharedPref.getBoolean("is_english", true)
        
        val locale = if (isEnglish) Locale("en") else Locale("it")
        Locale.setDefault(locale)
        
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    // start of the Activity (start of UI and GameManager)
    override fun onCreate(savedInstanceState: Bundle?) {
        if (isChangingTheme) {
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        super.onCreate(savedInstanceState)
        gameManager = GameManager(this)
        
        val mode = if (gameManager.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    // called whenever the Activity becomes visible
    override fun onResume() {
        super.onResume()
        isChangingTheme = false
        makeImmersive()
    }

    // listener of whenever the Activity loses focus
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            makeImmersive()
        }
    }

    // to make the Activity immersive (fullscreen, no status bar, no navigation bar)
    private fun makeImmersive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            @Suppress("DEPRECATION")
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            controller?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
    }

    // menu buttons setup and listeners
    protected fun setupMenuButtons() {

        // THEME
        findViewById<ImageView>(R.id.btnTheme)?.let { btn ->
            updateThemeIcon(btn)
            btn.setOnClickListener {
                if (isSpamming() || isChangingTheme) return@setOnClickListener

                isChangingTheme = true
                onBeforeThemeChanged()

                // flip animation at the end
                btn.animate().rotationY(90f).setDuration(150).withEndAction {
                    gameManager.isDarkMode = !gameManager.isDarkMode
                    val mode = if (gameManager.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                    AppCompatDelegate.setDefaultNightMode(mode)
                }.start()
            }
        }

        // LANGUAGE
        findViewById<ImageView>(R.id.btnLanguage)?.let { btn ->
            btn.setOnClickListener {
                if (isSpamming() || isChangingTheme) return@setOnClickListener

                // flip animation at the end
                btn.animate().rotationY(90f).setDuration(150).withEndAction {
                    btn.rotationY = -90f
                    btn.animate().rotationY(0f).setDuration(150).start()
                    onLanguageChanged()
                }.start()
            }
        }

        // COLORBLIND
        findViewById<ImageView>(R.id.btnColorblind)?.let { btn ->
            updateColorblindIcon(btn)
            btn.setOnClickListener {
                if (isSpamming() || isChangingTheme) return@setOnClickListener
                gameManager.isColorblindMode = !gameManager.isColorblindMode

                // flip animation at the end
                btn.animate().rotationY(90f).setDuration(150).withEndAction {
                    updateColorblindIcon(btn)
                    btn.rotationY = -90f
                    btn.animate().rotationY(0f).setDuration(150).start()
                    onColorblindModeChanged(gameManager.isColorblindMode)
                }.start()
            }
        }

        // RECORDS
        findViewById<ImageView>(R.id.btnRecords)?.setOnClickListener {
            if (isSpamming() || isChangingTheme) return@setOnClickListener
            if (this !is RecordsActivity) {
                val intent = Intent(this, RecordsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
            } else {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
            }
        }

        // SETTINGS
        findViewById<ImageView>(R.id.btnSettings)?.setOnClickListener {
            if (isSpamming() || isChangingTheme) return@setOnClickListener
            if (this !is SettingsActivity) {
                val intent = Intent(this, SettingsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
            } else {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
            }
        }
    }

    // spamming check to avoid crash
    private fun isSpamming(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < DEBOUNCE_TIME) {
            return true
        }
        lastClickTime = currentTime
        return false
    }

    // changes the theme icon when tapped
    private fun updateThemeIcon(btn: ImageView) {
        if (gameManager.isDarkMode) {
            btn.setImageResource(R.drawable.darkmode_icon)
        } else {
            btn.setImageResource(R.drawable.lightmode_icon)
        }
    }

    // changes the colorblind icon when tapped
    private fun updateColorblindIcon(btn: ImageView) {
        if (gameManager.isColorblindMode) {
            btn.setImageResource(R.drawable.view_icon) 
        } else {
            btn.setImageResource(R.drawable.hide_icon)
        }
    }

    // like abstract, but open allow to not override the fun
    protected open fun onLanguageChanged() {}
    protected open fun onColorblindModeChanged(enabled: Boolean) {}
    protected open fun onBeforeThemeChanged() {}
}
