package com.example.personaltasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val deadline: String,
    val completed: Boolean = false,
    val deleted: Boolean = false,
    val priority: String = "BAIXA"
) : Serializable
