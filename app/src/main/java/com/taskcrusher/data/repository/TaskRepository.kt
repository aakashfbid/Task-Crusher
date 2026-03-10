package com.taskcrusher.data.repository

import androidx.lifecycle.LiveData
import com.taskcrusher.data.database.TaskDao
import com.taskcrusher.data.models.Task

class TaskRepository(private val taskDao: TaskDao) {

    val activeTasks: LiveData<List<Task>> = taskDao.getActiveTasks()
    val completedTasks: LiveData<List<Task>> = taskDao.getCompletedTasks()
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)

    suspend fun markComplete(id: Long) = taskDao.markComplete(id)

    suspend fun snoozeTask(id: Long, newTime: Long) = taskDao.snoozeTask(id, newTime)

    suspend fun getActiveAlarmedTasks(): List<Task> = taskDao.getActiveAlarmedTasks()

    suspend fun getCompletedCountSince(since: Long): Int = taskDao.getCompletedCountSince(since)

    suspend fun getTotalSnoozesSince(since: Long): Int = taskDao.getTotalSnoozesSince(since) ?: 0

    suspend fun getMostSnoozedTask(since: Long): Task? = taskDao.getMostSnoozedTask(since)

    suspend fun getCompletedTasksSince(since: Long): List<Task> = taskDao.getCompletedTasksSince(since)

    suspend fun getOverdueCount(): Int = taskDao.getOverdueCount()
}
