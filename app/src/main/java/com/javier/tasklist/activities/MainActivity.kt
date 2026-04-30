package com.javier.tasklist.activities

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.javier.tasklist.R
import com.javier.tasklist.adapters.CategoryAdapter
import com.javier.tasklist.data.Category
import com.javier.tasklist.data.CategoryDAO
import com.javier.tasklist.databinding.ActivityMainBinding
import com.javier.tasklist.databinding.DialogCreateCategoryBinding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.javier.tasklist.data.MotivationPhrasePDAO
import com.javier.tasklist.data.MotivationalPhrase
import com.javier.tasklist.utils.PhraseSeeder

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var adapter: CategoryAdapter

    var categoryList: List<Category> = emptyList()

    lateinit var categoryDAO: CategoryDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PhraseSeeder().seedIfNeeded(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        categoryDAO = CategoryDAO(this)

        categoryList = categoryDAO.getAll()

        adapter = CategoryAdapter(categoryList, ::showCategory, ::editCategory, ::deleteCategory)

        binding.recyclerView.adapter = adapter

        binding.addCategoryFAB.setOnClickListener {
            showCategoryDialog(Category(-1, ""))
        }

        //Swipe para eliminar
        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.bindingAdapterPosition

                    if (position != RecyclerView.NO_POSITION) {
                        deleteCategory(position)
                    }
                }


            }
        )

        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        //Mostrar la frase motivadora
        val phrasePDAO = MotivationPhrasePDAO(this)
        val phrase = phrasePDAO.getRandom()

        phrase?.let {
            binding.phraseTextView.text = "\"${it.text}\""
            binding.idPhraseTexView.text = it.id.toString()
        }

    }

    fun showCategoryDialog(category: Category) {
        val dialogBinding = DialogCreateCategoryBinding.inflate(layoutInflater)

        val isEditing = category.id != -1

        val title: String
        val icon: Int

        if (isEditing) {
            title = "Editar categoría"
            icon = R.drawable.ic_edit
        } else {
            title = "Crear categoría"
            icon = R.drawable.ic_add
        }

        dialogBinding.textField.editText!!.setText(category.name)

        val dialog = MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.ic_category)
            .setTitle(title)
            .setView(dialogBinding.root)
            .setPositiveButton("Guardar") { dialog, which ->
                val name = dialogBinding.textField.editText!!.text.toString()
                category.name = name
                categoryDAO.save(category)
                categoryList = categoryDAO.getAll()
                adapter.updateData(categoryList)
            }
            .setNegativeButton("Cancelar") { dialog, which ->

            }
            //.setCancelable(false)
            .create()

        dialog.show()
    }

    fun showCategory(position: Int) {
        val category = categoryList[position]
        //Toast.makeText(this, category.name, Toast.LENGTH_SHORT).show() --- esto es para mensaje tipo alert
        val intent = Intent(this, TaskListActivity::class.java)
        intent.putExtra(TaskListActivity.EXTRA_CATEGORY_ID, category.id)
        startActivity(intent)
    }

    fun editCategory(position: Int) {
        val category = categoryList[position]
        showCategoryDialog(category)
    }

    fun deleteCategory(position: Int) {
        val category = categoryList[position]

        MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.ic_delete)
            .setTitle("Borrar categoría")
            .setMessage("¿Seguro que quieres borrar \"${category.name}\"?")
            .setPositiveButton("Sí") { _, _ ->
                categoryDAO.delete(category)
                refreshList()
            }
            .setNegativeButton("Cancelar") { _, _ ->
                refreshList()
            }
            .show()
    }

    private fun refreshList() {
        categoryList = categoryDAO.getAll()
        adapter.updateData(categoryList)
    }


}