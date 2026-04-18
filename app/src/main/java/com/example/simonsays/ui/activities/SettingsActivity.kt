package com.example.simonsays.ui.activities

import android.os.Bundle

import com.example.simonsays.R

class SettingsActivity : BaseActivity() {

    // TODO
    // start of the Activity (settings)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupMenuButtons()
    }
}
