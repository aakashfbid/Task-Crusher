package com.taskcrusher.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.taskcrusher.R
import com.taskcrusher.databinding.ActivityAlarmPopupBinding
import com.taskcrusher.service.AlarmService
import com.taskcrusher.ui.TaskViewModel
import com.taskcrusher.utils.AlarmScheduler
import com.taskcrusher.utils.PersonalityManager
import kotlinx.coroutines.launch
import java.util.*

class AlarmPopupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmPopupBinding
    private val viewModel: TaskViewModel by viewModels()
    private var taskId: Long = -1L
    private var snoozeCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )

        binding = ActivityAlarmPopupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskId = intent.getLongExtra(AlarmScheduler.EXTRA_TASK_ID, -1L)
        val taskTitle = intent.getStringExtra(AlarmScheduler.EXTRA_TASK_TITLE) ?: "Your Task"
        val taskDesc = intent.getStringExtra(AlarmScheduler.EXTRA_TASK_DESC) ?: ""

        setupUI(taskTitle, taskDesc)
        loadTaskDetails()
    }

    private fun loadTaskDetails() {
        lifecycleScope.launch {
            val task = viewModel.getTaskById(taskId)
            task?.let {
                snoozeCount = it.snoozeCount
                applyUrgencyTheme(snoozeCount)
                applyPersonalityMessage(snoozeCount)
            }
        }
    }

    private fun setupUI(title: String, desc: String) {
        binding.tvTaskTitle.text = title
        binding.tvTaskDesc.text = desc.ifEmpty { "Time to get this done!" }

        binding.btnAccept.setOnClickListener {
            stopAlarmService()
            viewModel.markComplete(taskId)
            finish()
        }

        binding.btnSnooze.setOnClickListener {
            showSnoozeMenu()
        }
    }

    private fun applyUrgencyTheme(snoozeCount: Int) {
        val (bgColor, borderColor, btnColor) = when {
            snoozeCount == 0 -> Triple(
                Color.parseColor("#0D2137"),
                Color.parseColor("#00FFFF"),
                Color.parseColor("#00FFFF")
            )
            snoozeCount <= 2 -> Triple(
                Color.parseColor("#1A1000"),
                Color.parseColor("#FF8C00"),
                Color.parseColor("#FF8C00")
            )
            else -> Triple(
                Color.parseColor("#1A0000"),
                Color.parseColor("#DC143C"),
                Color
