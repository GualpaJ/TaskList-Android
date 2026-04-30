package com.javier.tasklist.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.javier.tasklist.R
import com.javier.tasklist.adapters.TaskAdapter
import com.javier.tasklist.data.Category
import com.javier.tasklist.data.CategoryDAO
import com.javier.tasklist.data.Task
import com.javier.tasklist.data.TaskDAO
import com.javier.tasklist.databinding.ActivityTaskListBinding

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        categoryDAO = CategoryDAO(this)
        taskDAO = TaskDAO(this)

        val categoryId = intent.getIntExtra(EXTRA_CATEGORY_ID, -1)
        category = categoryDAO.getById(categoryId)

        // Crear tarea de prueba
        /*val task1 = Task(-1, "Comprar azulejos", false, category!!)
        val task2 = Task(-1, "Comprar cemento", false, category!!)
        val task3 = Task(-1, "Comprar clavos", false, category!!)
        taskDAO.insert(task1)
        taskDAO.insert(task2)
        taskDAO.insert(task3)*/
        // Fin codigo para pruebas

        category?.let {
            taskList = taskDAO.getAllByCategory(it)
        }

        adapter = TaskAdapter(taskList, ::showTask, ::editTask, ::deleteTask)
        binding.recyclerView.adapter = adapter
    }

    fun showTask(position: Int) {
        val task = taskList[position]

        task.done = !task.done
        taskDAO.update(task)

        taskList = taskDAO.getAllByCategory(category!!)
        adapter.updateData(taskList)
    }

    fun editTask(position: Int) {
        val task = taskList[position]

    }

    fun deleteTask(position: Int) {
        val task = taskList[position]

        val dialog = MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.ic_delete)
            .setTitle("Borrar tarea")
            .setMessage("¿Está usted seguro de querer borrar la tarea \"${task.title}\"?")
            .setPositiveButton("Si") { dialog, which ->
                taskDAO.delete(task)
                taskList = taskDAO.getAllByCategory(category!!)
                adapter.updateData(taskList)
            }
            .setNegativeButton("Cancelar") { dialog, which ->

            }
            //.setCancelable(false)
            .create()

        dialog.show()
    }
}