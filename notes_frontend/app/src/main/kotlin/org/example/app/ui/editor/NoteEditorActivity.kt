package org.example.app.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.example.app.R
import org.example.app.viewmodel.NoteDetailViewModel

/**
 * PUBLIC_INTERFACE
 * NoteEditorActivity
 * Activity for creating or editing a note. Accepts EXTRA_NOTE_ID for edit mode.
 */
class NoteEditorActivity : AppCompatActivity() {

    private val vm: NoteDetailViewModel by viewModels()

    private lateinit var titleInput: androidx.appcompat.widget.AppCompatEditText
    private lateinit var contentInput: androidx.appcompat.widget.AppCompatEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_OceanProfessional)
        setContentView(R.layout.activity_note_editor)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.editor_title)

        titleInput = findViewById(R.id.input_title)
        contentInput = findViewById(R.id.input_content)

        val id = intent.getLongExtra(EXTRA_NOTE_ID, -1L)
        if (id > 0) {
            vm.load(id)
        }

        lifecycleScope.launch {
            vm.note.collectLatest { note ->
                if (note != null) {
                    titleInput.setText(note.title)
                    contentInput.setText(note.content)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> { finish(); true }
            R.id.action_save -> {
                vm.save(
                    title = titleInput.text?.toString()?.trim().orEmpty(),
                    content = contentInput.text?.toString()?.trim().orEmpty(),
                    categoryId = null
                ) { _ ->
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_NOTE_ID = "extra_note_id"
    }
}
