package com.taskcrusher.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val alarmTimeMillis: Long = 0L,
    val isCompleted: Boolean = false,
    val snoozeCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long = 0L,
    val hasAlarm: Boolean = false
) {
    fun getUrgencyLevel(): UrgencyLevel {
        return when {
            snoozeCount == 0 -> UrgencyLevel.CALM
            snoozeCount <= 2 -> UrgencyLevel.WARNING
            else -> UrgencyLevel.CRITICAL
        }
    }
}

enum class UrgencyLevel {
    CALM,
    WARNING,
    CRITICAL
}
