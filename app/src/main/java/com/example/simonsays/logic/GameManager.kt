package com.example.simonsays.logic

import android.content.Context
import android.content.SharedPreferences

class GameManager(context: Context) {
    private val sharedPref: SharedPreferences = context.getSharedPreferences("SimonSaysPrefs", Context.MODE_PRIVATE)

    fun saveSequence(sequence: List<Pair<String, Int>>) {
        if (sequence.isEmpty()) return
        val serialized = sequence.joinToString(",") { "${it.first}:${it.second}" }
        sharedPref.edit().putString("saved_sequence", serialized).apply()
    }

    fun loadSavedSequence(): List<Pair<String, Int>> {
        val saved = sharedPref.getString("saved_sequence", null) ?: return emptyList()
        if (saved.isEmpty()) return emptyList()

        return saved.split(",").mapNotNull { item ->
            val parts = item.split(":")
            if (parts.size == 2) {
                parts[0] to parts[1].toInt()
            } else null
        }
    }

    fun clearSequence() {
        sharedPref.edit().remove("saved_sequence").apply()
    }

    var isColorblindMode: Boolean
        get() = sharedPref.getBoolean("colorblind_mode", false)
        set(value) = sharedPref.edit().putBoolean("colorblind_mode", value).apply()

    var isDarkMode: Boolean
        get() = sharedPref.getBoolean("dark_mode", false)
        set(value) = sharedPref.edit().putBoolean("dark_mode", value).apply()
}
