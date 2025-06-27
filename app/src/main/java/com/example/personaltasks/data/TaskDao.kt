package com.example.personaltasks.data

import androidx.room.*

@Dao
interface TaskDao {
    @Query("SELECT * FROM task_table WHERE deleted = 0")
    suspend fun getAllTasks(): List<Task>

    @Query("SELECT * FROM task_table WHERE deleted = 1")
    suspend fun getDeletedTasks(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(task: Task)

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}
