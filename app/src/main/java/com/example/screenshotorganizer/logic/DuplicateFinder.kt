package com.example.screenshotorganizer.logic

import com.example.screenshotorganizer.data.ScreenshotEntity

object DuplicateFinder {

    private const val OLD_DAYS = 90L

    fun findDuplicates(items: List<ScreenshotEntity>): List<ScreenshotEntity> {
        val groups = items.filter { it.size > 0 }.groupBy { it.size }
        return groups.values
            .filter { it.size > 1 }
            .flatMap { group -> group.sortedBy { it.dateTaken }.drop(1) }
    }

    fun findOld(items: List<ScreenshotEntity>): List<ScreenshotEntity> {
        val limit = System.currentTimeMillis() - OLD_DAYS * 24L * 60L * 60L * 1000L
        return items.filter { it.dateTaken < limit }
    }

    fun findAllToReview(items: List<ScreenshotEntity>): List<ScreenshotEntity> {
        return (findDuplicates(items) + findOld(items)).distinctBy { it.id }
    }
}
