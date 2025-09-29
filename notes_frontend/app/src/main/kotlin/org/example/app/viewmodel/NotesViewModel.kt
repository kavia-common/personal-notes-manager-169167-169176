package org.example.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.app.data.Note
import org.example.app.data.NotesRepository

/**
 * ViewModel for Notes list and operations.
 */
@kotlinx.coroutines.ExperimentalCoroutinesApi
class NotesViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = NotesRepository.get(app)

    private val query = MutableStateFlow("")
    val notes: StateFlow<List<Note>> = query
        .flatMapLatest { q -> if (q.isBlank()) repo.observeNotes() else repo.observeNotesQuery(q) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setQuery(text: String) {
        viewModelScope.launch { query.emit(text) }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch { repo.deleteNote(id) }
    }
}
