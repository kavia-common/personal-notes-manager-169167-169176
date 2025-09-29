package org.example.app.data

/**
 * Plain data model for a note.
 */
data class Note(
    val id: Long = 0L,
    val title: String,
    val content: String,
    val categoryId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
