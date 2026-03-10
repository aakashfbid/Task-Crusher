package com.taskcrusher.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.taskcrusher.data.database.TaskDatabase
import com.taskcrusher.data.repository.TaskRepository
import com.taskcrusher.utils.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = TaskDatabase.getInstance(context)
                    val repo = TaskRepository(db.taskDao())
                    val tasks = repo.getActiveAlarmedTasks()
                    val now = System.currentTimeMillis()

                    tasks.forEach { task ->
                        if (task.alarmTimeMillis > now) {
                            AlarmScheduler.scheduleAlarm(context, task)
                        }
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
