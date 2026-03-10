package com.taskcrusher.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.taskcrusher.data.models.Task

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY alarmTimeMillis ASC")
    fun getActiveTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND hasAlarm = 1")
    suspend fun getActiveAlarmedTasks(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("UPDATE tasks SET isCompleted = 1, completedAt = :completedAt WHERE id = :id")
    suspend fun markComplete(id: Long, completedAt: Long = System.currentTimeMillis())

    @Query("UPDATE tasks SET snoozeCount = snoozeCount + 1, alarmTimeMillis = :newTime WHERE id = :id")
    suspend fun snoozeTask(id: Long, newTime: Long)

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1 AND completedAt >= :since")
    suspend fun getCompletedCountSince(since: Long): Int

    @Query("SELECT SUM(snoozeCount) FROM tasks WHERE createdAt >= :since")
    suspend fun getTotalSnoozesSince(since: Long): Int

    @Query("SELECT * FROM tasks WHERE createdAt >= :since ORDER BY snoozeCount DESC LIMIT 1")
    suspend fun getMostSnoozedTask(since: Long): Task?

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 AND completedAt >= :since")
    suspend fun getCompletedTasksSince(since: Long): List<Task>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0 AND hasAlarm = 1 AND alarmTimeMillis < :now")
    suspend fun getOverdueCount(now: Long = System.currentTimeMillis()): Int
}
