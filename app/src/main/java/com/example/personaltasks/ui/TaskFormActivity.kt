package com.example.personaltasks.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.personaltasks.R
import com.example.personaltasks.data.Task
import com.example.personaltasks.data.TaskDatabase
import kotlinx.coroutines.launch


class TaskFormActivity : AppCompatActivity() {
    private lateinit var edtTitle: EditText
    private lateinit var edtDescription: EditText
    private lateinit var txtSelectedDate: TextView
    private lateinit var btnPickDate: Button
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var switchCompleted: Switch

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
        switchCompleted = findViewById(R.id.switchCompleted)

        val received = intent.getSerializableExtra("task")
        viewOnly = intent.getBooleanExtra("viewOnly", false)

        if (received is Task) {
            editingTask = received
            edtTitle.setText(received.title)
            edtDescription.setText(received.description)
            txtSelectedDate.text = received.deadline
            selectedDate = received.deadline
            switchCompleted.isChecked = received.completed
        }

        if (viewOnly) {
            edtTitle.isEnabled = false
            edtDescription.isEnabled = false
            btnPickDate.isEnabled = false
            btnSave.visibility = View.GONE
            switchCompleted.isEnabled = false
        }

        btnPickDate.setOnClickListener {
            val datePicker = DatePickerDialog(this)
            datePicker.setOnDateSetListener { _, year, month, day ->
                selectedDate = "%02d/%02d/%04d".format(day, month + 1, year)
                txtSelectedDate.text = selectedDate
            }
            datePicker.show()
        }

        btnSave.setOnClickListener {
            val title = edtTitle.text.toString()
            val description = edtDescription.text.toString()
            val deadline = selectedDate
            val completed = switchCompleted.isChecked

            val newTask = Task(
                id = editingTask?.id ?: 0,
                title = title,
                description = description,
                deadline = deadline,
                completed = completed
            )

            lifecycleScope.launch {
                val dao = TaskDatabase.getDatabase(this@TaskFormActivity).taskDao()
                if (editingTask != null) dao.update(newTask)
                else dao.insert(newTask)
                finish()
            }
        }

        btnCancel.setOnClickListener { finish() }
    }
}
