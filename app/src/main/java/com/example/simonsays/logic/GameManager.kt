package com.example.simonsays.logic

import android.content.Context
import android.content.SharedPreferences

import androidx.core.content.edit

class GameManager(context: Context) {

    data class SequenceElement(val label: String, val color: Int, val isError: Boolean = false)

    // information on the new score
    data class HistoryEntry(
        val score: Int, 
        val sequence: List<SequenceElement>,
        val isRunning: Boolean = false,
        val playerIndex: Int = 0,
        val targetSequenceLabels: List<String> = emptyList(),
        val isPaused: Boolean = false,
        val timestamp: Long = 0L,
        val repetitionsAllowed: Boolean = true
    )

    private val sharedPref: SharedPreferences = context.getSharedPreferences("SimonSaysPrefs", Context.MODE_PRIVATE)

    // set and add the new sequence for the history
    fun addSequence(score: Int, sequence: List<SequenceElement>, timestamp: Long, repetitionsAllowed: Boolean) {
        if (sequence.isEmpty() && score == 0) return
        val serializedSeq = sequence.joinToString(",") { "${it.label}:${it.color}:${if (it.isError) 1 else 0}" }
        val repInt = if (repetitionsAllowed) 1 else 0

        // format: score;serializedSeq;isRunning;playerIndex;targetLabels;isPaused;timestamp;repetitionsAllowed
        val entry = "$score;$serializedSeq;0;0;;0;$timestamp;$repInt"
        
        val historyStr = sharedPref.getString("history_list", "") ?: ""
        val newHistory = if (historyStr.isEmpty()) {
            entry
        } else {
            entry + "|" + historyStr.split("|").take(49).joinToString("|")
        }
        
        sharedPref.edit {
            putString("history_list", newHistory)
            remove("saved_sequence") // Clear draft when game is finished
        }
    }

    // save sequence draft in sharedPref
    fun saveDraft(score: Int, playerIndex: Int, isRunning: Boolean, targetLabels: List<String>, sequence: List<SequenceElement>, isPaused: Boolean = false) {
        val serializedSeq = sequence.joinToString(",") { "${it.label}:${it.color}:${if (it.isError) 1 else 0}" }
        val targetStr = targetLabels.joinToString(",")
        val runningInt = if (isRunning) 1 else 0
        val pausedInt = if (isPaused) 1 else 0
        val repInt = if (isRepetitionAllowed) 1 else 0
        // draft format: score;serializedSeq;isRunning;playerIndex;targetLabels;isPaused;timestamp;repetitionsAllowed
        val draft = "$score;$serializedSeq;$runningInt;$playerIndex;$targetStr;$pausedInt;0;$repInt"
        sharedPref.edit { putString("saved_sequence", draft) }
    }

    // load draft from sharedPref
    fun loadSavedSequence(): HistoryEntry {
        val saved = sharedPref.getString("saved_sequence", null) ?: return HistoryEntry(0, emptyList())
        return deserializeEntry(saved)
    }

    // splits each element from the sequence for history
    private fun deserializeEntry(serialized: String): HistoryEntry {
        if (serialized.isEmpty()) return HistoryEntry(0, emptyList())
        
        val parts = serialized.split(";")
        val score = try { parts[0].toInt() } catch (_: Exception) { 0 }
        
        val sequenceStr = if (parts.size > 1) parts[1] else ""
        val sequence = if (sequenceStr.isEmpty()) emptyList() else {
            sequenceStr.split(",").mapNotNull { item ->
                val itemParts = item.split(":")
                if (itemParts.size >= 2) {
                    try {
                        val label = itemParts[0]
                        val color = itemParts[1].toInt()
                        val isError = if (itemParts.size >= 3) itemParts[2] == "1" else false
                        SequenceElement(label, color, isError)
                    } catch (_: Exception) { null }
                } else null
            }
        }

        val isRunning = if (parts.size > 2) parts[2] == "1" else false
        val playerIndex = if (parts.size > 3) parts[3].toIntOrNull() ?: 0 else 0
        val targetLabels = if (parts.size > 4 && parts[4].isNotEmpty()) parts[4].split(",") else emptyList()
        val isPaused = if (parts.size > 5) parts[5] == "1" else false
        val timestamp = if (parts.size > 6) parts[6].toLongOrNull() ?: 0L else 0L
        val repetitionsAllowed = if (parts.size > 7) parts[7] == "1" else true

        return HistoryEntry(score, sequence, isRunning, playerIndex, targetLabels, isPaused, timestamp, repetitionsAllowed)
    }

    // returns the whole sequence history
    fun getAllSequences(): List<HistoryEntry> {
        val historyStr = sharedPref.getString("history_list", "") ?: return emptyList()
        if (historyStr.isEmpty()) return emptyList()
        return historyStr.split("|").map { deserializeEntry(it) }
    }

    // removes the draft from sharedPref
    fun clearSequence() {
        sharedPref.edit { remove("saved_sequence") }
    }

    // clears the entire history
    fun clearHistory() {
        sharedPref.edit { remove("history_list") }
    }

    // from settings, resets to default
    fun resetToDefault() {
        val wasRunning = loadSavedSequence().isRunning
        val currentRepAllowed = isRepetitionAllowed
        sharedPref.edit(commit = true) {
            putBoolean("is_english", true)
            putBoolean("colorblind_mode", true)
            putBoolean("is_dark_mode", false)
            putBoolean("sound_enabled", true)
            // Disable changing repetitions via reset if a game is running
            if (!wasRunning) {
                putBoolean("is_repetition_allowed", true)
            } else {
                putBoolean("is_repetition_allowed", currentRepAllowed)
            }
            putInt("sound_volume", 50)
            putFloat("game_speed", 1.0f)
        }
    }

    // getter/setter for language in EN
    var isEnglishLanguage: Boolean
        get() = sharedPref.getBoolean("is_english", true)
        set(value) = sharedPref.edit { putBoolean("is_english", value) }

    // getter/setter for colorblind mode
    var isColorblindMode: Boolean
        get() = sharedPref.getBoolean("colorblind_mode", true)
        set(value) = sharedPref.edit { putBoolean("colorblind_mode", value) }

    // getter/setter for dark mode
    var isDarkMode: Boolean
        get() = sharedPref.getBoolean("is_dark_mode", false)
        set(value) = sharedPref.edit { putBoolean("is_dark_mode", value) }

    // getter/setter for repetitions
    var isRepetitionAllowed: Boolean
        get() = sharedPref.getBoolean("is_repetition_allowed", true)
        set(value) = sharedPref.edit { putBoolean("is_repetition_allowed", value) }

    // getter/setter for sound volume
    var soundVolume: Int
        get() = sharedPref.getInt("sound_volume", 50)
        set(value) = sharedPref.edit { putInt("sound_volume", value) }

    // getter/setter for game speed
    var gameSpeed: Float
        get() = sharedPref.getFloat("game_speed", 1.0f)
        set(value) = sharedPref.edit { putFloat("game_speed", value) }
}
