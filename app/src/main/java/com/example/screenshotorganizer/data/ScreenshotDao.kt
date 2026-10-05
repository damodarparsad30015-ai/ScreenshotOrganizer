package com.example.screenshotorganizer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScreenshotDao {

    @Query("SELECT * FROM screenshots ORDER BY dateTaken DESC")
    fun getAll(): Flow<List<ScreenshotEntity>>

    @Query("SELECT id FROM screenshots")
    suspend fun getAllIds(): List<Long>

    @Query("SELECT * FROM screenshots WHERE ocrDone = 0 LIMIT 20")
    suspend fun getPendingOcr(): List<ScreenshotEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<ScreenshotEntity>)

    @Query("UPDATE screenshots SET text = :text, category = :category, ocrDone = 1 WHERE id = :id")
    suspend fun updateOcr(id: Long, text: String, category: String)

    @Query("UPDATE screenshots SET category = :category WHERE id = :id")
    suspend fun updateCategory(id: Long, category: String)

    @Query("DELETE FROM screenshots WHERE id = :id")
    suspend fun deleteById(id: Long)
}
