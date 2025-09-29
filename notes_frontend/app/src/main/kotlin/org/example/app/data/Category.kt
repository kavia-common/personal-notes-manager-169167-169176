package org.example.app.data

/**
 * Plain data model for a category.
 */
data class Category(
    val id: Long = 0L,
    val name: String,
    val colorHex: String? = null
)
