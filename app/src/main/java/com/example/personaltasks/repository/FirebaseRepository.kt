package com.example.personaltasks.repository

import com.example.personaltasks.data.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("tasks")

    suspend fun syncUp(task: Task) {
        collection.document(task.id.toString()).set(task).await()
    }

    suspend fun deleteRemote(task: Task) {
        collection.document(task.id.toString()).delete().await()
    }

    suspend fun fetchAll(): List<Task> {
        val snapshot = collection.get().await()
        return snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
    }
}
