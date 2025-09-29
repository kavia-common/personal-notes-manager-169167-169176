package org.example.app.widgets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.example.app.R
import org.example.app.data.Category
import org.example.app.data.CategoryWithCount

class CategoriesAdapter(
    private val onDelete: (Category) -> Unit
) : ListAdapter<CategoryWithCount, CategoryViewHolder>(CatDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(v, onDelete)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object CatDiff : DiffUtil.ItemCallback<CategoryWithCount>() {
    override fun areItemsTheSame(oldItem: CategoryWithCount, newItem: CategoryWithCount) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: CategoryWithCount, newItem: CategoryWithCount) = oldItem == newItem
}

class CategoryViewHolder(
    itemView: View,
    private val onDelete: (Category) -> Unit
) : RecyclerView.ViewHolder(itemView) {
    private val name: TextView = itemView.findViewById(R.id.category_name)
    private val count: TextView = itemView.findViewById(R.id.category_count)
    private val deleteBtn: ImageButton = itemView.findViewById(R.id.btn_delete_category)

    fun bind(cat: CategoryWithCount) {
        name.text = cat.name
        count.text = itemView.context.getString(R.string.notes_count, cat.notesCount)
        deleteBtn.setOnClickListener {
            onDelete(Category(id = cat.id, name = cat.name, colorHex = cat.colorHex))
        }
    }
}
