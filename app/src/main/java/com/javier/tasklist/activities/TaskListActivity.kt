package com.javier.tasklist.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.javier.tasklist.R
import com.javier.tasklist.adapters.TaskAdapter
import com.javier.tasklist.data.*
import com.javier.tasklist.databinding.ActivityTaskListBinding
import com.javier.tasklist.databinding.DialogCreateTaskBinding

class TaskListActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY_ID = "CATEGORY_ID"
    }

    lateinit var binding: ActivityTaskListBinding

    lateinit var categoryDAO: CategoryDAO
    lateinit var taskDAO: TaskDAO

    var category: Category? = null
    var taskList: List<Task> = emptyList()

    lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        categoryDAO = CategoryDAO(this)
        taskDAO = TaskDAO(this)

        val categoryId = intent.getIntExtra(EXTRA_CATEGORY_ID, -1)
        category = categoryDAO.getById(categoryId)

        updateTaskCount()

        category?.let {
            taskList = taskDAO.getAllByCategory(it)
        }

        adapter = TaskAdapter(
            taskList,
            { pos, isChecked -> toggleTaskDone(pos, isChecked) },
            { pos -> editTask(pos) }
        )

        binding.recyclerView.adapter = adapter

        binding.addTaskFAB.setOnClickListener {
            showTaskDialog(Task(-1, "", false, category!!))
        }

        // SWIPE DELETE
        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val pos = viewHolder.bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        deleteTask(pos)
                    }
                }
            }
        )

        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }


    fun toggleTaskDone(position: Int, isChecked: Boolean) {
        val task = taskList[position]
        task.done = isChecked
        taskDAO.update(task)

        //adapter.notifyItemChanged(position)-- esto crashea
        binding.recyclerView.post {
            adapter.notifyItemChanged(position)
        }

    }


    fun editTask(position: Int) {
        val task = taskList[position]
        showTaskDialog(task)
    }

    fun deleteTask(position: Int) {
        val task = taskList[position]

        MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.ic_delete)
            .setTitle("Borrar tarea")
            .setMessage("¿Seguro que quieres borrar \"${task.title}\"?")
            .setPositiveButton("Sí") { _, _ ->
                taskDAO.delete(task)
                refreshList()
            }
            .setNegativeButton("Cancelar") { _, _ ->
                refreshList()
            }
            .show()
    }

    private fun refreshList() {
        category?.let {
            taskList = taskDAO.getAllByCategory(it)
            adapter.updateData(taskList)
            updateTaskCount()
        }
    }

    fun showTaskDialog(task: Task) {
        val dialogBinding = DialogCreateTaskBinding.inflate(layoutInflater)
        val isEditing = task.id != -1

        dialogBinding.textField.editText!!.setText(task.title)

        MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.ic_category)
            .setTitle(if (isEditing) "Editar tarea" else "Nueva tarea")
            .setView(dialogBinding.root)
            .setPositiveButton("Guardar") { _, _ ->
                task.title = dialogBinding.textField.editText!!.text.toString()
                taskDAO.save(task)
                refreshList()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun updateTaskCount() {
        category?.let {
            val count = taskDAO.getTaskCountForThisCategory(it.id)
            binding.taskCountTextView.text = count.toString()
        }
    }
}