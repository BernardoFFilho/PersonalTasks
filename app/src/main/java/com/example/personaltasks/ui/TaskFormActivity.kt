package com.example.personaltasks.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.personaltasks.R
import com.example.personaltasks.data.Task
import com.example.personaltasks.data.TaskDatabase
import kotlinx.coroutines.launch
import java.util.*

class TaskFormActivity : AppCompatActivity() {

    private lateinit var edtTitle: EditText
    private lateinit var edtDescription: EditText
    private lateinit var txtSelectedDate: TextView
    private lateinit var btnPickDate: Button
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var selectedDate: String = ""
    private var editingTask: Task? = null
    private var viewOnly: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_form)

        edtTitle = findViewById(R.id.edtTitle)
        edtDescription = findViewById(R.id.edtDescription)
        txtSelectedDate = findViewById(R.id.txtSelectedDate)
        btnPickDate = findViewById(R.id.btnPickDate)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        // Verifica se veio uma tarefa para edição ou visualização
        val received = intent.getSerializableExtra("task")
        viewOnly = intent.getBooleanExtra("viewOnly", false)

        if (received is Task) {
            editingTask = received
            edtTitle.setText(received.title)
            edtDescription.setText(received.description)
            txtSelectedDate.text = received.deadline
            selectedDate = received.deadline
        }

        if (viewOnly) {
            edtTitle.isEnabled = false
            edtDescription.isEnabled = false
            btnPickDate.isEnabled = false
            btnSave.visibility = View.GONE
        }

        btnPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                    txtSelectedDate.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.show()
        }

        btnSave.setOnClickListener {
            val title = edtTitle.text.toString().trim()
            val description = edtDescription.text.toString().trim()
            val deadline = selectedDate.trim()

            if (title.isEmpty() || description.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val task = Task(
                id = editingTask?.id ?: 0,
                title = title,
                description = description,
                deadline = deadline
            )

            lifecycleScope.launch {
                val dao = TaskDatabase.getDatabase(this@TaskFormActivity).taskDao()
                if (editingTask != null) {
                    dao.update(task)
                } else {
                    dao.insert(task)
                }
                finish()
            }
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }
}
