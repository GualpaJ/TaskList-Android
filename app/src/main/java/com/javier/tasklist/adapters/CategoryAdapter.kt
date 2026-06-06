package com.javier.tasklist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javier.tasklist.data.Category
import com.javier.tasklist.databinding.ItemCategoryBinding

class CategoryAdapter(
    var items: List<Category>,
    var taskCounts: Map<Int, Int>,
    val onClick: (Int) -> Unit,
    val onEdit: (Int) -> Unit,
    val onDelete: (Int) -> Unit,
) : RecyclerView.Adapter<CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoryBinding.inflate(layoutInflater, parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = items[position]
        val taskCount = taskCounts[category.id] ?: 0

        holder.render(category, taskCount)

        holder.itemView.setOnClickListener {
            onClick(position)
        }

        holder.itemView.setOnLongClickListener {
            onEdit(position)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(dataSet: List<Category>) {
        items = dataSet
        notifyDataSetChanged()
    }

    fun updateData(dataSet: List<Category>, newTaskCounts: Map<Int, Int>) {
        items = dataSet
        taskCounts = newTaskCounts
        notifyDataSetChanged()
    }
}

class CategoryViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

    fun render(category: Category, taskCount: Int) {
        binding.titleTextView.text = category.name

        // muestra el número de tareas
        val tasksText = if (taskCount == 1) "$taskCount Tarea" else "$taskCount Tareas"
        binding.subtitleText.text = tasksText
    }

}