package org.example.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.example.app.R
import org.example.app.data.Category
import org.example.app.viewmodel.CategoriesViewModel
import org.example.app.widgets.CategoriesAdapter
import org.example.app.widgets.ListDividerDecoration

/**
 * Categories screen for managing categories.
 */
class CategoriesFragment : Fragment() {

    private val viewModel: CategoriesViewModel by viewModels()
    private lateinit var recycler: androidx.recyclerview.widget.RecyclerView
    private lateinit var addBtn: ImageButton
    private val adapter by lazy { CategoriesAdapter(::onDeleteClick) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.recycler_categories)
        addBtn = view.findViewById(R.id.btn_add_category)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.addItemDecoration(ListDividerDecoration(requireContext()))
        recycler.adapter = adapter

        lifecycleScope.launch {
            viewModel.categories.collectLatest {
                adapter.submitList(it)
            }
        }

        addBtn.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun showAddCategoryDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_category, null)
        val nameInput = dialogView.findViewById<AppCompatEditText>(R.id.input_category_name)

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_category)
            .setView(dialogView)
            .setPositiveButton(R.string.add) { d, _ ->
                val name = nameInput.text?.toString()?.trim().orEmpty()
                if (name.isNotEmpty()) {
                    viewModel.addCategory(name, null)
                }
                d.dismiss()
            }
            .setNegativeButton(R.string.cancel) { d, _ -> d.dismiss() }
            .show()
    }

    private fun onDeleteClick(category: Category) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_category)
            .setMessage(getString(R.string.delete_category_confirm, category.name))
            .setPositiveButton(R.string.delete) { d, _ ->
                viewModel.deleteCategory(category)
                d.dismiss()
            }
            .setNegativeButton(R.string.cancel) { d, _ -> d.dismiss() }
            .show()
    }

    companion object {
        fun newInstance() = CategoriesFragment()
    }
}
