package com.example.screenshotorganizer.logic

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.example.screenshotorganizer.data.ScreenshotEntity

object ScreenshotScanner {

    fun scan(context: Context): List<ScreenshotEntity> {
        val result = mutableListOf<ScreenshotEntity>()
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.RELATIVE_PATH
        )
        val selection =
            "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ? OR " +
                "${MediaStore.Images.Media.DISPLAY_NAME} LIKE ?"
        val args = arrayOf("%Screenshots%", "Screenshot%")

        context.contentResolver.query(
            collection,
            projection,
            selection,
            args,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val uri = ContentUris.withAppendedId(collection, id)
                result.add(
                    ScreenshotEntity(
                        id = id,
                        uri = uri.toString(),
                        name = cursor.getString(nameCol) ?: "",
                        dateTaken = cursor.getLong(dateCol) * 1000L,
                        size = cursor.getLong(sizeCol)
                    )
                )
            }
        }
        return result
    }
}
