package com.javier.tasklist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javier.tasklist.data.Task
import com.javier.tasklist.databinding.ItemTaskBinding

class TaskAdapter(
    var items: List<Task>,
    val onToggleDone: (Int, Boolean) -> Unit,  // Solo para el checkbox
    val onEdit: (Int) -> Unit,                 // Para long click
) : RecyclerView.Adapter<TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(inflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = items[position]

        holder.render(task)

        // Click normal → marcar/desmarcar checkbox
        holder.itemView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                val newState = !items[pos].done
                onToggleDone(pos, newState)
            }
        }

        // Long click → editar
        holder.itemView.setOnLongClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onEdit(pos)
            }
            true
        }

        holder.binding.doneCheckBox.setOnCheckedChangeListener(null)
        holder.binding.doneCheckBox.isChecked = task.done

        holder.binding.doneCheckBox.setOnCheckedChangeListener { _, isChecked ->
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onToggleDone(pos, isChecked)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(dataSet: List<Task>) {
        items = dataSet
        notifyDataSetChanged()
    }
}

class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

    fun render(task: Task) {
        binding.titleTextView.text = task.title
        binding.doneCheckBox.isChecked = task.done
    }
}