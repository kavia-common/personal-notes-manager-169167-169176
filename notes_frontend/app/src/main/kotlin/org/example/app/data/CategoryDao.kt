package org.example.app.data

/**
 * CategoryWithCount holder used by repository when listing categories.
 */
data class CategoryWithCount(
    val id: Long,
    val name: String,
    val colorHex: String?,
    val notesCount: Int
)
