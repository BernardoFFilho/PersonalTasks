package com.example.personaltasks.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personaltasks.R
import com.example.personaltasks.adapter.TaskAdapter
import com.example.personaltasks.data.Task
import com.example.personaltasks.data.TaskDatabase
import com.example.personaltasks.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerTasks: RecyclerView
    private lateinit var adapter: TaskAdapter
    private var tasks: MutableList<Task> = mutableListOf()
    private var selectedTask: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerTasks = findViewById(R.id.recyclerTasks)
        recyclerTasks.layoutManager = LinearLayoutManager(this)

        loadTasks()
        registerForContextMenu(recyclerTasks)
    }

    private fun loadTasks() {
        lifecycleScope.launch {
            val dao = TaskDatabase.getDatabase(this@MainActivity).taskDao()
            val repository = FirebaseRepository()

            val remoteTasks = repository.fetchAll()
            remoteTasks.forEach { dao.upsert(it) }

            tasks.clear()
            tasks.addAll(dao.getAllTasks())

            adapter = TaskAdapter(tasks) { task, view ->
                selectedTask = task
                openContextMenu(view)
            }

            recyclerTasks.adapter = adapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                startActivity(Intent(this, TaskFormActivity::class.java))
                true
            }
            R.id.menu_trash -> {
                startActivity(Intent(this, ExcludedTasksActivity::class.java))
                true
            }
            R.id.menu_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.setHeaderTitle("Opções da Tarefa")
        menu.add(0, 1, 1, "Editar")
        menu.add(0, 2, 2, "Excluir")
        menu.add(0, 3, 3, "Detalhes")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val task = selectedTask ?: return false
        return when (item.itemId) {
            1 -> {
                val intent = Intent(this, TaskFormActivity::class.java)
                intent.putExtra("task", task)
                startActivity(intent)
                true
            }
            2 -> {
                lifecycleScope.launch {
                    val dao = TaskDatabase.getDatabase(this@MainActivity).taskDao()
                    val updatedTask = task.copy(deleted = true)
                    dao.update(updatedTask)
                    FirebaseRepository().syncUp(updatedTask)
                    loadTasks()
                }
                true
            }
            3 -> {
                val intent = Intent(this, TaskFormActivity::class.java)
                intent.putExtra("task", task)
                intent.putExtra("viewOnly", true)
                startActivity(intent)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }
}
