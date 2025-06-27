package com.example.personaltasks.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.personaltasks.R
import com.example.personaltasks.data.Task
import com.example.personaltasks.data.TaskDatabase
import com.example.personaltasks.repository.FirebaseRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TaskFormActivity : AppCompatActivity() {

    private lateinit var edtTitle: EditText
    private lateinit var edtDescription: EditText
    private lateinit var txtSelectedDate: TextView
    private lateinit var btnSave: Button

    private var selectedDate: String = ""
    private var taskToEdit: Task? = null
    private var viewOnly: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_form)

        edtTitle = findViewById(R.id.edtTitle)
        edtDescription = findViewById(R.id.edtDescription)
        txtSelectedDate = findViewById(R.id.txtSelectedDate)
        btnSave = findViewById(R.id.btnSave)

        val calendar = Calendar.getInstance()
        updateDateText(calendar)

        txtSelectedDate.setOnClickListener {
            if (!viewOnly) {
                DatePickerDialog(
                    this,
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        updateDateText(calendar)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }

        taskToEdit = intent.getSerializableExtra("task") as? Task
        viewOnly = intent.getBooleanExtra("viewOnly", false)

        if (taskToEdit != null) {
            edtTitle.setText(taskToEdit!!.title)
            edtDescription.setText(taskToEdit!!.description)
            txtSelectedDate.text = taskToEdit!!.deadline
            selectedDate = taskToEdit!!.deadline

            if (viewOnly) {
                edtTitle.isEnabled = false
                edtDescription.isEnabled = false
                txtSelectedDate.isEnabled = false
                btnSave.isEnabled = false
                btnSave.text = "Visualização"
            }
        }

        btnSave.setOnClickListener {
            val title = edtTitle.text.toString().trim()
            val description = edtDescription.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newTask = Task(
                id = taskToEdit?.id ?: 0,
                title = title,
                description = description,
                deadline = selectedDate,
                completed = taskToEdit?.completed ?: false,
                deleted = taskToEdit?.deleted ?: false
            )

            lifecycleScope.launch {
                val dao = TaskDatabase.getDatabase(this@TaskFormActivity).taskDao()
                if (taskToEdit == null) {
                    dao.insert(newTask)
                } else {
                    dao.update(newTask)
                }
                FirebaseRepository().syncUp(newTask)
                finish()
            }
        }
    }

    private fun updateDateText(calendar: Calendar) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        selectedDate = sdf.format(calendar.time)
        txtSelectedDate.text = selectedDate
    }
}
