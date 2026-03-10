package com.taskcrusher.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.*
import androidx.core.app.NotificationCompat
import com.taskcrusher.R
import com.taskcrusher.ui.activities.AlarmPopupActivity
import com.taskcrusher.utils.AlarmScheduler

class AlarmService : Service() {

    companion object {
        const val CHANNEL_ID_ALARM = "alarm_channel"
        const val CHANNEL_ID_TASKS = "tasks_channel"
        const val NOTIFICATION_ID_ALARM = 1001
        const val ACTION_STOP_ALARM = "com.taskcrusher.ACTION_STOP_ALARM"

        fun createNotificationChannels(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val alarmChannel = NotificationChannel(
                    CHANNEL_ID_ALARM,
                    "Task Alarms",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Critical task alarm notifications"
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
                    setBypassDnd(true)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                val tasksChannel = NotificationChannel(
                    CHANNEL_ID_TASKS,
                    "Task Updates",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "General task notifications"
                }
                notificationManager.createNotificationChannel(alarmChannel)
                notificationManager.createNotificationChannel(tasksChannel)
            }
        }
    }

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var taskId: Long = -1L

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        taskId = intent?.getLongExtra(AlarmScheduler.EXTRA_TASK_ID, -1L) ?: -1L
        val taskTitle = intent?.getStringExtra(AlarmScheduler.EXTRA_TASK_TITLE) ?: "Task Alarm"
        val taskDesc = intent?.getStringExtra(AlarmScheduler.EXTRA_TASK_DESC) ?: ""

        if (intent?.action == ACTION_STOP_ALARM) {
            stopAlarm()
            return START_NOT_STICKY
        }

        startForeground(NOTIFICATION_ID_ALARM, buildAlarmNotification(taskTitle, taskId))
        startRinging()
        launchPopupActivity(taskId, taskTitle, taskDesc)
        return START_STICKY
    }

    private fun buildAlarmNotification(title: String, taskId: Long): Notification {
        val fullScreenIntent = Intent(this, AlarmPopupActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
            putExtra(AlarmScheduler.EXTRA_TASK_ID, taskId)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, taskId.toInt(), fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_STOP_ALARM
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID_ALARM)
            .setSmallIcon(R.drawable.ic_task_notification)
            .setContentTitle("TASK ALARM: $title")
            .setContentText("Time to crush it!")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(false)
            .setOngoing(true)
            .addAction(R.drawable.ic_check, "Dismiss", stopPendingIntent)
            .build()
    }

    private fun launchPopupActivity(taskId: Long, title: String, desc: String) {
        val popupIntent = Intent(this, AlarmPopupActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_NO_USER_ACTION or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(AlarmScheduler.EXTRA_TASK_ID, taskId)
            putExtra(AlarmScheduler.EXTRA_TASK_TITLE, title)
            putExtra(AlarmScheduler.EXTRA_TASK_DESC, desc)
        }
        startActivity(popupIntent)
    }

    private fun startRinging() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        val pattern = longArrayOf(0, 800, 400, 800, 400, 800)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
