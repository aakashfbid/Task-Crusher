package com.taskcrusher.ui.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.taskcrusher.data.models.Task
import com.taskcrusher.databinding.ActivityAddTaskBinding
import com.taskcrusher.ui.TaskViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private val viewModel: TaskViewModel by viewModels()
    private var selectedTimeMillis: Long = 0L
    private var existingTaskId: Long = -1L
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        existingTaskId = intent.getLongExtra("task_id", -1L)

        if (existingTaskId != -1L) {
            loadExistingTask()
        } else {
            supportActionBar?.title = "Brain Dump"
        }

        setupClickListeners()
    }

    private fun loadExistingTask() {
        lifecycleScope.launch {
            val task = viewModel.getTaskById(existingTaskId)
            task?.let {
                binding.etTaskTitle.setText(it.title)
                binding.etTaskDescription.setText(it.description)
                if (it.alarmTimeMillis > 0) {
                    selectedTimeMillis = it.alarmTimeMillis
                    binding.switchAlarm.isChecked = it.hasAlarm
                    updateTimeDisplay()
                }
                supportActionBar?.title = "Edit Task"
            }
        }
    }

    private fun setupClickListeners() {
        binding.switchAlarm.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutAlarmTime.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (isChecked && selectedTimeMillis == 0L) {
                openDateTimePicker()
            }
        }

        binding.btnPickTime.setOnClickListener {
            openDateTimePicker()
        }

        binding.btnSaveTask.setOnClickListener {
            saveTask()
        }
    }

    private fun openDateTimePicker() {
        val now = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                TimePickerDialog(
                    this,
                    { _, hour, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)
                        selectedTimeMillis = calendar.timeInMillis
                        updateTimeDisplay()
                    },
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    false
                ).show()
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateTimeDisplay() {
        val sdf = SimpleDateFormat("EEE, MMM d h:mm a", Locale.getDefault())
