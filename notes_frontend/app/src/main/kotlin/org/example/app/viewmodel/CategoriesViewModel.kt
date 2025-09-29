package org.example.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.example.app.data.Category
import org.example.app.data.CategoryWithCount
import org.example.app.data.NotesRepository

/**
 * ViewModel for Categories screen.
 */
class CategoriesViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = NotesRepository.get(app)

    val categories = repo.observeCategoriesWithCounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCategory(name: String, color: String?) {
        viewModelScope.launch { repo.addCategory(name, color) }
    }

    fun deleteCategory(cat: Category) {
        viewModelScope.launch { repo.deleteCategory(cat) }
    }
}
