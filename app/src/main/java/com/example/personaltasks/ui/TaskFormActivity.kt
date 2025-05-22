package com.example.personaltasks.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.personaltasks.R

// imports...

class TaskFormActivity : AppCompatActivity() {
    // campos: edtTitle, edtDescription, txtSelectedDate, etc.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_form)

        // lógica para salvar/editar tarefa e devolver via Intent
    }
}
