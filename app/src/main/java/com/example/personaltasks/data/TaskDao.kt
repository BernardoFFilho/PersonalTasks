package com.example.personaltasks.data

import androidx.room.*

@Dao
interface TaskDao {
    @Query("SELECT * FROM task_table")
    suspend fun getAllTasks(): List<Task>

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}