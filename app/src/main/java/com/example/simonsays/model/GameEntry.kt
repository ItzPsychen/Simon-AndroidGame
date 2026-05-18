package com.example.simonsays.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_entries")
data class GameEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val score: Int,                     // score of the game
    val serializedSequence: String,     // sequence serialized as string
    val isRunning: Boolean,             // TRUE or FALSE if game is running
    val playerIndex: Int,               // actual player index
    val targetLabels: String,           // target labels as string
    val isPaused: Boolean,              // TRUE or FALSE if game is paused
    val timestamp: Long,                // time date-hour
    val repetitionsAllowed: Boolean,    // TRUE or FALSE if repetitions are allowed
    val isDraft: Boolean = false        // TRUE or FALSE if this is a draft
)
