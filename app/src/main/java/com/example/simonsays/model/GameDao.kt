package com.example.simonsays.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameDao {
    // selects all history (no draft)
    @Query("SELECT * FROM game_entries WHERE isDraft = 0 ORDER BY timestamp DESC")
    suspend fun getAllHistory(): List<GameEntry>

    // only one draft at a time
    @Query("SELECT * FROM game_entries WHERE isDraft = 1 LIMIT 1")
    suspend fun getDraft(): GameEntry?

    // used to replace a game with same id (used for draft)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: GameEntry)

    // delete draft
    @Query("DELETE FROM game_entries WHERE isDraft = 1")
    suspend fun deleteDraft()

    // delete all
    @Query("DELETE FROM game_entries WHERE isDraft = 0")
    suspend fun deleteHistory()

    // "suspend" used to make the task run in background
}
