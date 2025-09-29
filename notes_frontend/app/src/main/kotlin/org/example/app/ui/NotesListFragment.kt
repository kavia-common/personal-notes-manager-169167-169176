package org.example.app.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.example.app.R
import org.example.app.viewmodel.NotesViewModel
import org.example.app.widgets.ListDividerDecoration
import org.example.app.widgets.NoteListAdapter

/**
 * Notes list fragment displaying all notes with search and delete.
 */
class NotesListFragment : Fragment() {

    private val viewModel: NotesViewModel by viewModels()
    private lateinit var recycler: RecyclerView
    private lateinit var emptyView: View
    private lateinit var searchInput: androidx.appcompat.widget.AppCompatEditText
    private val adapter by lazy { NoteListAdapter(::onNoteClick) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_notes_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.recycler)
        emptyView = view.findViewById(R.id.empty_view)
        searchInput = view.findViewById(R.id.input_search)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter
        recycler.addItemDecoration(ListDividerDecoration(requireContext()))

        val ith = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val note = adapter.currentList.getOrNull(vh.bindingAdapterPosition) ?: return
                viewModel.deleteNote(note.id)
            }
        })
        ith.attachToRecyclerView(recycler)

        lifecycleScope.launch {
            viewModel.notes.collectLatest {
                adapter.submitList(it)
                emptyView.isVisible = it.isEmpty()
            }
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setQuery(s?.toString().orEmpty())
            }
        })
    }

    private fun onNoteClick(id: Long) {
        val intent = Intent(requireContext(), NoteEditorActivity::class.java)
        intent.putExtra(NoteEditorActivity.EXTRA_NOTE_ID, id)
        startActivity(intent)
    }

    companion object {
        fun newInstance() = NotesListFragment()
    }
}
