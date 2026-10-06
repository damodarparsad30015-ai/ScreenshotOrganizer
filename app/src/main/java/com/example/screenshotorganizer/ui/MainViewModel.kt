package com.example.screenshotorganizer.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.screenshotorganizer.data.AppDatabase
import com.example.screenshotorganizer.data.ScreenshotEntity
import com.example.screenshotorganizer.logic.Categorizer
import com.example.screenshotorganizer.logic.DuplicateFinder
import com.example.screenshotorganizer.logic.OcrHelper
import com.example.screenshotorganizer.logic.ScreenshotScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.get(app).dao()

    val query = MutableStateFlow("")
    val category = MutableStateFlow("All Screenshots")

    val all: StateFlow<List<ScreenshotEntity>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val filtered: StateFlow<List<ScreenshotEntity>> =
        combine(all, query, category) { list, q, cat ->
            list.filter { item ->
                val catOk = cat == "All Screenshots" || item.category == cat
                val text = q.trim().lowercase()
                val qOk = text.isEmpty() ||
                    item.text.lowercase().contains(text) ||
                    item.name.lowercase().contains(text) ||
                    item.category.lowercase().contains(text)
                catOk && qOk
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val toReview: StateFlow<List<ScreenshotEntity>> = all
        .combine(MutableStateFlow(0)) { list, _ -> DuplicateFinder.findAllToReview(list) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun scan() {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            val found = ScreenshotScanner.scan(context)
            val known = dao.getAllIds().toSet()
            val fresh = found.filter { it.id !in known }
            if (fresh.isNotEmpty()) dao.insertAll(fresh)
            runOcr()
        }
    }

    private suspend fun runOcr() {
        val context = getApplication<Application>()
        while (true) {
            val pending = dao.getPendingOcr()
            if (pending.isEmpty()) break
            for (item in pending) {
                val text = OcrHelper.readText(context, Uri.parse(item.uri))
                dao.updateOcr(item.id, text, Categorizer.categorize(text))
            }
        }
    }

    fun changeCategory(id: Long, newCategory: String) {
        viewModelScope.launch(Dispatchers.IO) { dao.updateCategory(id, newCategory) }
    }

    fun removeFromList(id: Long) {
        viewModelScope.launch(Dispatchers.IO) { dao.deleteById(id) }
    }

    suspend fun deleteFromPhone(item: ScreenshotEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            val rows = getApplication<Application>().contentResolver
                .delete(Uri.parse(item.uri), null, null)
            if (rows > 0) dao.deleteById(item.id)
            rows > 0
        } catch (e: Exception) {
            false
        }
    }
}
