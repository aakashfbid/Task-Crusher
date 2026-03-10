package com.taskcrusher.ui

import android.app.Application
import androidx.lifecycle.*
import com.taskcrusher.data.database.TaskDatabase
import com.taskcrusher.data.models.Task
import com.taskcrusher.data.repository.TaskRepository
import com.taskcrusher.utils.AlarmScheduler
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    val activeTasks: LiveData<List<Task>>
    val completedTasks: LiveData<List<Task>>

    init {
        val db = TaskDatabase.getInstance(application)
        repository = TaskRepository(db.taskDao())
        activeTasks = repository.activeTasks
        completedTasks = repository.completedTasks
    }

    fun insertTask(task: Task) = viewModelScope.launch {
        val id = repository.insertTask(task)
        if (task.hasAlarm && task.alarmTimeMillis > System.currentTimeMillis()) {
            val taskWithId = task.copy(id = id)
            AlarmScheduler.scheduleAlarm(getApplication(), taskWithId)
        }
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task)
        if (task.hasAlarm && task.alarmTimeMillis > System.currentTimeMillis()) {
            AlarmScheduler.scheduleAlarm(getApplication(), task)
        } else {
            AlarmScheduler.cancelAlarm(getApplication(), task.id)
        }
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        AlarmScheduler.cancelAlarm(getApplication(), task.id)
        repository.deleteTask(task)
    }

    fun markComplete(taskId: Long) = viewModelScope.launch {
        AlarmScheduler.cancelAlarm(getApplication(), taskId)
        repository.markComplete(taskId)
    }

    fun snoozeTask(taskId: Long, newTime: Long) = viewModelScope.launch {
        repository.snoozeTask(taskId, newTime)
        val task = repository.getTaskById(taskId)
        task?.let {
            val snoozed = it.copy(alarmTimeMillis = newTime)
            AlarmScheduler.scheduleAlarm(getApplication(), snoozed)
        }
    }

    suspend fun getTaskById(id: Long): Task? = repository.getTaskById(id)

    suspend fun getWeeklyStats(): WeeklyStats {
        val weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        val completed = repository.getCompletedCountSince(weekAgo)
        val totalSnoozes = repository.getTotalSnoozesSince(weekAgo)
        val mostSnoozed = repository.getMostSnoozedTask(weekAgo)
        val completedTasks = repository.getCompletedTasksSince(weekAgo)
        return WeeklyStats(completed, totalSnoozes, mostSnoozed, completedTasks)
    }

    suspend fun getMonthlyStats(): WeeklyStats {
        val monthAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000L)
        val completed = repository.getCompletedCountSince(monthAgo)
        val totalSnoozes = repository.getTotalSnoozesSince(monthAgo)
        val mostSnoozed = repository.getMostSnoozedTask(monthAgo)
        val completedTasks = repository.getCompletedTasksSince(monthAgo)
        return WeeklyStats(completed, totalSnoozes, mostSnoozed, completedTasks)
    }
}

data class WeeklyStats(
    val completedCount: Int,
    val totalSnoozes: Int,
    val mostSnoozedTask: Task?,
    val completedTasks: List<Task>
)
