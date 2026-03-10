package com.taskcrusher.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.taskcrusher.service.AlarmService
import com.taskcrusher.utils.AlarmScheduler

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AlarmScheduler.ACTION_ALARM_TRIGGER) {
            val taskId = intent.getLongExtra(AlarmScheduler.EXTRA_TASK_ID, -1L)
            val taskTitle = intent.getStringExtra(AlarmScheduler.EXTRA_TASK_TITLE) ?: "Task"
            val taskDesc = intent.getStringExtra(AlarmScheduler.EXTRA_TASK_DESC) ?: ""

            if (taskId == -1L) return

            val serviceIntent = Intent(context, AlarmService::class.java).apply {
                putExtra(AlarmScheduler.EXTRA_TASK_ID, taskId)
                putExtra(AlarmScheduler.EXTRA_TASK_TITLE, taskTitle)
                putExtra(AlarmScheduler.EXTRA_TASK_DESC, taskDesc)
            }
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}
