package org.example.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.app.data.Note
import org.example.app.data.NotesRepository

/**
 * ViewModel for creating/editing a single note.
 */
class NoteDetailViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = NotesRepository.get(app)

    private val _note = MutableStateFlow<Note?>(null)
    val note: StateFlow<Note?> = _note

    fun load(id: Long) {
        viewModelScope.launch {
            _note.value = repo.getNote(id)
        }
    }

    fun save(title: String, content: String, categoryId: Long?, onSaved: (Long) -> Unit) {
        viewModelScope.launch {
            val existing = _note.value
            if (existing == null) {
                val id = repo.addNote(title, content, categoryId)
                onSaved(id)
            } else {
                repo.updateNote(existing.copy(title = title, content = content, categoryId = categoryId))
                onSaved(existing.id)
            }
        }
    }
}
