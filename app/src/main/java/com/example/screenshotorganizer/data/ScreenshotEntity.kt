package com.example.screenshotorganizer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screenshots")
data class ScreenshotEntity(
    @PrimaryKey val id: Long,
    val uri: String,
    val name: String,
    val dateTaken: Long,
    val size: Long,
    val text: String = "",
    val category: String = "Others",
    val ocrDone: Boolean = false
)
