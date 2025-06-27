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
import kotlinx.coroutines.launch

class ExcludedTasksActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private var excludedTasks: MutableList<Task> = mutableListOf()
    private var selectedTask: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_excluded_tasks)

        recyclerView = findViewById(R.id.recyclerExcludedTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadExcludedTasks()
        registerForContextMenu(recyclerView)
    }

    private fun loadExcludedTasks() {
        lifecycleScope.launch {
            val dao = TaskDatabase.getDatabase(this@ExcludedTasksActivity).taskDao()
            excludedTasks.clear()
            excludedTasks.addAll(dao.getDeletedTasks())

            adapter = TaskAdapter(excludedTasks) { task, view ->
                selectedTask = task
                openContextMenu(view)
            }

            recyclerView.adapter = adapter
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.setHeaderTitle("Opções da Tarefa")
        menu.add(0, 1, 1, "Reativar tarefa")
        menu.add(0, 2, 2, "Detalhes")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val task = selectedTask ?: return false
        return when (item.itemId) {
            1 -> {
                lifecycleScope.launch {
                    val dao = TaskDatabase.getDatabase(this@ExcludedTasksActivity).taskDao()
                    val restoredTask = task.copy(deleted = false)
                    dao.update(restoredTask)
                    FirebaseRepository().syncUp(restoredTask)
                    loadExcludedTasks()
                }
                true
            }
            2 -> {
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
        loadExcludedTasks()
    }
}
