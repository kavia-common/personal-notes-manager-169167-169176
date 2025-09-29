package org.example.app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * SQLiteOpenHelper for notes and categories.
 */
class DbHelper(context: Context) : SQLiteOpenHelper(context, "notes-db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE categories(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                colorHex TEXT
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE notes(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                categoryId INTEGER,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL,
                isDeleted INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(categoryId) REFERENCES categories(id) ON DELETE SET NULL
            )
        """.trimIndent())
        db.execSQL("CREATE INDEX idx_notes_category ON notes(categoryId)")
        db.execSQL("CREATE INDEX idx_notes_updated ON notes(updatedAt)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle future migrations
    }
}
