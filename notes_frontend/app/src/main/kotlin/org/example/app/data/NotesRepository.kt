package org.example.app.data

import android.content.ContentValues
import android.content.Context
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

/**
 * Repository exposing high-level data operations for notes and categories using SQLiteOpenHelper.
 * Emits changes via a simple in-memory notifier.
 */
class NotesRepository private constructor(context: Context) {

    private val helper = DbHelper(context)
    private val listeners = mutableListOf<() -> Unit>()

    private fun notifyChanged() {
        listeners.toList().forEach { it.invoke() }
    }

    fun observeNotes(): Flow<List<Note>> = callbackFlow {
        val listener: () -> Unit = { trySend(listNotes(null)).isSuccess }
        listeners.add(listener)
        trySend(listNotes(null))
        awaitClose { listeners.remove(listener) }
    }

    fun observeNotesQuery(query: String): Flow<List<Note>> = callbackFlow {
        val listener: () -> Unit = { trySend(listNotes(query)).isSuccess }
        listeners.add(listener)
        trySend(listNotes(query))
        awaitClose { listeners.remove(listener) }
    }.onStart { emit(listNotes(null)) }

    suspend fun getNote(id: Long): Note? {
        val db = helper.readableDatabase
        db.rawQuery(
            "SELECT id,title,content,categoryId,createdAt,updatedAt,isDeleted FROM notes WHERE id=?",
            arrayOf(id.toString())
        ).use { c ->
            if (c.moveToFirst()) {
                return Note(
                    id = c.getLong(0),
                    title = c.getString(1),
                    content = c.getString(2),
                    categoryId = if (c.isNull(3)) null else c.getLong(3),
                    createdAt = c.getLong(4),
                    updatedAt = c.getLong(5),
                    isDeleted = c.getInt(6) == 1
                )
            }
        }
        return null
    }

    suspend fun addNote(title: String, content: String, categoryId: Long?): Long {
        val now = System.currentTimeMillis()
        val values = ContentValues().apply {
            put("title", title)
            put("content", content)
            if (categoryId != null) put("categoryId", categoryId)
            put("createdAt", now)
            put("updatedAt", now)
            put("isDeleted", 0)
        }
        val id = helper.writableDatabase.insert("notes", null, values)
        notifyChanged()
        return id
    }

    suspend fun updateNote(note: Note) {
        val values = ContentValues().apply {
            put("title", note.title)
            put("content", note.content)
            if (note.categoryId != null) put("categoryId", note.categoryId) else putNull("categoryId")
            put("updatedAt", System.currentTimeMillis())
        }
        helper.writableDatabase.update("notes", values, "id=?", arrayOf(note.id.toString()))
        notifyChanged()
    }

    suspend fun deleteNote(id: Long) {
        val values = ContentValues().apply { put("isDeleted", 1) }
        helper.writableDatabase.update("notes", values, "id=?", arrayOf(id.toString()))
        notifyChanged()
    }

    fun observeCategories(): Flow<List<Category>> = callbackFlow {
        val listener: () -> Unit = { trySend(listCategories()).isSuccess }
        listeners.add(listener)
        trySend(listCategories())
        awaitClose { listeners.remove(listener) }
    }

    fun observeCategoriesWithCounts(): Flow<List<CategoryWithCount>> = callbackFlow {
        val listener: () -> Unit = { trySend(listCategoriesWithCounts()).isSuccess }
        listeners.add(listener)
        trySend(listCategoriesWithCounts())
        awaitClose { listeners.remove(listener) }
    }

    suspend fun addCategory(name: String, colorHex: String?): Long {
        val values = ContentValues().apply {
            put("name", name)
            if (colorHex != null) put("colorHex", colorHex)
        }
        val id = helper.writableDatabase.insert("categories", null, values)
        notifyChanged()
        return id
    }

    suspend fun updateCategory(category: Category) {
        val values = ContentValues().apply {
            put("name", category.name)
            if (category.colorHex != null) put("colorHex", category.colorHex)
        }
        helper.writableDatabase.update("categories", values, "id=?", arrayOf(category.id.toString()))
        notifyChanged()
    }

    suspend fun deleteCategory(category: Category) {
        helper.writableDatabase.delete("categories", "id=?", arrayOf(category.id.toString()))
        notifyChanged()
    }

    private fun listNotes(query: String?): List<Note> {
        val db = helper.readableDatabase
        val sql = buildString {
            append("SELECT id,title,content,categoryId,createdAt,updatedAt,isDeleted FROM notes WHERE isDeleted=0")
            if (!query.isNullOrBlank()) append(" AND (title LIKE ? OR content LIKE ?)")
            append(" ORDER BY updatedAt DESC")
        }
        val args = if (!query.isNullOrBlank()) arrayOf("%$query%", "%$query%") else emptyArray()
        val res = mutableListOf<Note>()
        db.rawQuery(sql, args).use { c ->
            while (c.moveToNext()) {
                res.add(
                    Note(
                        id = c.getLong(0),
                        title = c.getString(1),
                        content = c.getString(2),
                        categoryId = if (c.isNull(3)) null else c.getLong(3),
                        createdAt = c.getLong(4),
                        updatedAt = c.getLong(5),
                        isDeleted = c.getInt(6) == 1
                    )
                )
            }
        }
        return res
    }

    private fun listCategories(): List<Category> {
        val db = helper.readableDatabase
        val res = mutableListOf<Category>()
        db.rawQuery("SELECT id,name,colorHex FROM categories ORDER BY name ASC", emptyArray()).use { c ->
            while (c.moveToNext()) {
                res.add(Category(id = c.getLong(0), name = c.getString(1), colorHex = c.getString(2)))
            }
        }
        return res
    }

    private fun listCategoriesWithCounts(): List<CategoryWithCount> {
        val db = helper.readableDatabase
        val res = mutableListOf<CategoryWithCount>()
        db.rawQuery(
            """
            SELECT c.id, c.name, c.colorHex, 
            (SELECT COUNT(*) FROM notes n WHERE n.isDeleted=0 AND n.categoryId = c.id) as cnt
            FROM categories c ORDER BY c.name ASC
            """.trimIndent(),
            emptyArray()
        ).use { c ->
            while (c.moveToNext()) {
                res.add(
                    CategoryWithCount(
                        id = c.getLong(0),
                        name = c.getString(1),
                        colorHex = c.getString(2),
                        notesCount = c.getInt(3)
                    )
                )
            }
        }
        return res
    }

    companion object {
        @Volatile private var INSTANCE: NotesRepository? = null
        fun get(context: Context): NotesRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: NotesRepository(context.applicationContext).also { INSTANCE = it }
            }
    }
}
