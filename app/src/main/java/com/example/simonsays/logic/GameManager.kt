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

    fun getAllSequences(): List<List<Pair<String, Int>>> {
        val historyStr = sharedPref.getString("history_list", "") ?: return emptyList()
        if (historyStr.isEmpty()) return emptyList()
        return historyStr.split("|").map { deserialize(it) }
    }

    fun loadSavedSequence(): List<Pair<String, Int>> {
        val saved = sharedPref.getString("saved_sequence", null) ?: return emptyList()
        return deserialize(saved)
    }

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

    fun clearSequence() {
        sharedPref.edit { remove("saved_sequence") }
    }

    // TODO
    // will be used in settings
    fun clearHistory() {
        sharedPref.edit { remove("history_list") }
    }

    var isColorblindMode: Boolean
        get() = sharedPref.getBoolean("colorblind_mode", true)
        set(value) = sharedPref.edit { putBoolean("colorblind_mode", value) }

    var isDarkMode: Boolean
        get() = sharedPref.getBoolean("is_dark_mode", false)
        set(value) = sharedPref.edit { putBoolean("is_dark_mode", value) }
}
