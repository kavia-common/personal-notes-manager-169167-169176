package org.example.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.example.app.R

/**
 * PUBLIC_INTERFACE
 * MainActivity
 * This is the main entry point showing bottom navigation with three tabs.
 * Tabs: All Notes, Categories, Settings. A Floating Action Button adds a new note.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_OceanProfessional_NoActionBar)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_nav)
        fab = findViewById(R.id.fab_add)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NotesListFragment.newInstance())
                .commit()
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tab_notes -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, NotesListFragment.newInstance())
                        .commit()
                    fab.show()
                    true
                }
                R.id.tab_categories -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CategoriesFragment.newInstance())
                        .commit()
                    fab.hide()
                    true
                }
                R.id.tab_settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SettingsFragment.newInstance())
                        .commit()
                    fab.hide()
                    true
                }
                else -> false
            }
        }

        fab.setOnClickListener {
            startActivity(Intent(this, NoteEditorActivity::class.java))
        }
    }
}
