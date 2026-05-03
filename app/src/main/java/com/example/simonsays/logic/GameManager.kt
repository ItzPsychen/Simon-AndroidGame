package com.example.simonsays.logic

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class GameManager(context: Context) {

    // sequences are stored in a protected area (only the app itself can access)
    private val sharedPref: SharedPreferences = context.getSharedPreferences("SimonSaysPrefs", Context.MODE_PRIVATE)

    fun addSequence(sequence: List<Pair<String, Int>>) {
        if (sequence.isEmpty()) return
        val serialized = sequence.joinToString(",") { "${it.first}:${it.second}" }
        
        val historyStr = sharedPref.getString("history_list", "") ?: ""
        val newHistory = if (historyStr.isEmpty()) {
            serialized
        } else {
            serialized + "|" + historyStr.split("|").take(49).joinToString("|")
        }
        
        sharedPref.edit {
            putString("history_list", newHistory)
            putString("saved_sequence", serialized)
        }
    }

    // save sequence as draft
    fun saveDraft(sequence: List<Pair<String, Int>>) {
        val serialized = sequence.joinToString(",") { "${it.first}:${it.second}" }
        sharedPref.edit { putString("saved_sequence", serialized) }
    }

    // helper function to return all sequences (after deserialization)
    fun getAllSequences(): List<List<Pair<String, Int>>> {
        val historyStr = sharedPref.getString("history_list", "") ?: return emptyList()
        if (historyStr.isEmpty()) return emptyList()
        return historyStr.split("|").map { deserialize(it) }
    }

    // function that loads the saved sequence (draft)
    fun loadSavedSequence(): List<Pair<String, Int>> {
        val saved = sharedPref.getString("saved_sequence", null) ?: return emptyList()
        return deserialize(saved)
    }

    // helper function to separate each sequence
    private fun deserialize(serialized: String): List<Pair<String, Int>> {
        if (serialized.isEmpty()) return emptyList()
        return serialized.split(",").mapNotNull { item ->
            val parts = item.split(":")
            if (parts.size == 2) {
                try {
                    parts[0] to parts[1].toInt()
                } catch (e: Exception) {
                    null
                }
            } else null
        }
    }

    // clear the saved sequence
    fun clearSequence() {
        sharedPref.edit { remove("saved_sequence") }
    }

    // delete all games
    fun clearHistory() {
        sharedPref.edit { remove("history_list") }
    }

    // reset settings to default one
    fun resetToDefault() {
        sharedPref.edit {
            putBoolean("is_english", true)
            putBoolean("colorblind_mode", true)
            putBoolean("is_dark_mode", false)
            putBoolean("sound_enabled", true)
            putInt("sound_volume", 50)
        }
    }

    // getter for language
    var isEnglishLanguage: Boolean
        get() = sharedPref.getBoolean("is_english", true)
        set(value) = sharedPref.edit { putBoolean("is_english", value) }

    // getter for colorblind mode
    var isColorblindMode: Boolean
        get() = sharedPref.getBoolean("colorblind_mode", true)
        set(value) = sharedPref.edit { putBoolean("colorblind_mode", value) }

    // getter for theme mode
    var isDarkMode: Boolean
        get() = sharedPref.getBoolean("is_dark_mode", false)
        set(value) = sharedPref.edit { putBoolean("is_dark_mode", value) }

    // getter and setter for sound effects volume
    var soundVolume: Int
        get() = sharedPref.getInt("sound_volume", 50)
        set(value) = sharedPref.edit { putInt("sound_volume", value) }
}
