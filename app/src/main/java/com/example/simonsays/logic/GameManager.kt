package com.example.simonsays.logic

import android.content.Context
import android.content.SharedPreferences

import androidx.core.content.edit

import com.example.simonsays.model.AppDatabase
import com.example.simonsays.model.GameEntry

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    private val db = AppDatabase.getDatabase(context)
    private val gameDao = db.gameDao()

    // DATABASE OPERATIONS

    // saves the final sequence
    suspend fun addSequence(score: Int, sequence: List<SequenceElement>, timestamp: Long, repetitionsAllowed: Boolean) = withContext(Dispatchers.IO) {
        if (sequence.isEmpty() && score == 0) return@withContext
        val serializedSeq = sequence.joinToString(",") { "${it.label}:${it.color}:${if (it.isError) 1 else 0}" }
        
        val entry = GameEntry(
            score = score,
            serializedSequence = serializedSeq,
            isRunning = false,
            playerIndex = 0,
            targetLabels = "",
            isPaused = false,
            timestamp = timestamp,
            repetitionsAllowed = repetitionsAllowed,
            isDraft = false
        )
        gameDao.insert(entry)
        gameDao.deleteDraft()
    }

    // saves the draft sequence
    suspend fun saveDraft(score: Int, playerIndex: Int, isRunning: Boolean, targetLabels: List<String>, sequence: List<SequenceElement>, isPaused: Boolean = false) = withContext(Dispatchers.IO) {
        val serializedSeq = sequence.joinToString(",") { "${it.label}:${it.color}:${if (it.isError) 1 else 0}" }
        val targetStr = targetLabels.joinToString(",")
        
        val draft = GameEntry(
            // fixed id for draft
            id = 1,
            score = score,
            serializedSequence = serializedSeq,
            isRunning = isRunning,
            playerIndex = playerIndex,
            targetLabels = targetStr,
            isPaused = isPaused,
            timestamp = System.currentTimeMillis(),
            repetitionsAllowed = isRepetitionAllowed,
            isDraft = true
        )

        // clear old draft and insert new one
        gameDao.deleteDraft()
        gameDao.insert(draft)
    }

    // loads a draft sequence
    suspend fun loadSavedSequence(): HistoryEntry = withContext(Dispatchers.IO) {
        val draft = gameDao.getDraft() ?: return@withContext HistoryEntry(0, emptyList())
        return@withContext mapToHistoryEntry(draft)
    }

    // map GameEntry to HistoryEntry
    private fun mapToHistoryEntry(entry: GameEntry): HistoryEntry {
        val sequence = if (entry.serializedSequence.isEmpty()) emptyList() else {
            entry.serializedSequence.split(",").mapNotNull { item ->
                val parts = item.split(":")
                if (parts.size >= 2) {
                    try {
                        SequenceElement(parts[0], parts[1].toInt(), parts.getOrNull(2) == "1")
                    } catch (_: Exception) { null }
                } else null
            }
        }
        val targetLabels = if (entry.targetLabels.isNotEmpty()) entry.targetLabels.split(",") else emptyList()
        
        return HistoryEntry(
            score = entry.score,
            sequence = sequence,
            isRunning = entry.isRunning,
            playerIndex = entry.playerIndex,
            targetSequenceLabels = targetLabels,
            isPaused = entry.isPaused,
            timestamp = entry.timestamp,
            repetitionsAllowed = entry.repetitionsAllowed
        )
    }

    // getter for the whole history
    suspend fun getAllSequences(): List<HistoryEntry> = withContext(Dispatchers.IO) {
        return@withContext gameDao.getAllHistory().map { mapToHistoryEntry(it) }
    }

    // delete draft sequence
    suspend fun clearSequence() = withContext(Dispatchers.IO) {
        gameDao.deleteDraft()
    }

    // delete all history
    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        gameDao.deleteHistory()
    }

    // SHARED-PREFERENCES OPERATIONS

    // set settings to default
    fun resetToDefault() {
        sharedPref.edit(commit = true) {
            putBoolean("is_english", true)
            putBoolean("colorblind_mode", true)
            putBoolean("is_dark_mode", false)
            putBoolean("sound_enabled", true)
            putBoolean("is_repetition_allowed", true)
            putInt("sound_volume", 50)
            putFloat("game_speed", 1.0f)
        }
    }

    // getter/setter for language settings
    var isEnglishLanguage: Boolean
        get() = sharedPref.getBoolean("is_english", true)
        set(value) = sharedPref.edit { putBoolean("is_english", value) }

    // getter/setter for colorblind mode
    var isColorblindMode: Boolean
        get() = sharedPref.getBoolean("colorblind_mode", true)
        set(value) = sharedPref.edit { putBoolean("colorblind_mode", value) }

    // getter/setter for theme mode
    var isDarkMode: Boolean
        get() = sharedPref.getBoolean("is_dark_mode", false)
        set(value) = sharedPref.edit { putBoolean("is_dark_mode", value) }

    // getter/setter for repetitions
    var isRepetitionAllowed: Boolean
        get() = sharedPref.getBoolean("is_repetition_allowed", true)
        set(value) = sharedPref.edit { putBoolean("is_repetition_allowed", value) }

    // getter/setter for sound
    var soundVolume: Int
        get() = sharedPref.getInt("sound_volume", 50)
        set(value) = sharedPref.edit { putInt("sound_volume", value) }

    // getter/setter for game speed
    var gameSpeed: Float
        get() = sharedPref.getFloat("game_speed", 1.0f)
        set(value) = sharedPref.edit { putFloat("game_speed", value) }
}
