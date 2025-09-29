package org.example.app.widgets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.example.app.R
import org.example.app.data.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val df = SimpleDateFormat("MMM d, yyyy • HH:mm", Locale.getDefault())

class NoteListAdapter(
    private val onClick: (Long) -> Unit
) : ListAdapter<Note, NoteViewHolder>(NoteDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(v, onClick)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object NoteDiff : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
}

class NoteViewHolder(
    itemView: View,
    private val onClick: (Long) -> Unit
) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.note_title)
    private val content: TextView = itemView.findViewById(R.id.note_content)
    private val meta: TextView = itemView.findViewById(R.id.note_meta)

    fun bind(note: Note) {
        title.text = if (note.title.isBlank()) itemView.context.getString(R.string.untitled) else note.title
        content.text = note.content
        meta.text = df.format(Date(note.updatedAt))
        itemView.setOnClickListener { onClick(note.id) }
    }
}
