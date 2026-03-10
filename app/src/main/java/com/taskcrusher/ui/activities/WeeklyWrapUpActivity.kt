package com.taskcrusher.ui.activities

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.taskcrusher.databinding.ActivityWeeklyWrapupBinding
import com.taskcrusher.ui.TaskViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class WeeklyWrapUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeeklyWrapupBinding
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeeklyWrapupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Weekly Wrap-Up"

        loadStats()

        binding.btnDownloadJpg.setOnClickListener {
            saveWrapUpAsJpg()
        }

        binding.rgPeriod.setOnCheckedChangeListener { _, _ ->
            loadStats()
        }
    }

    private fun loadStats() {
        lifecycleScope.launch {
            val isMonthly = binding.rbMonthly.isChecked
            val stats = if (isMonthly) viewModel.getMonthlyStats() else viewModel.getWeeklyStats()
            val period = if (isMonthly) "Monthly" else "Weekly"

            binding.tvHeadline.text = buildHeadline(stats.completedCount, stats.totalSnoozes, period)
            binding.tvMostSnoozed.text = stats.mostSnoozedTask?.let {
                "Most snoozed: \"${it.title}\" (${it.snoozeCount}x)"
            } ?: "No overdue tasks! You're a legend!"

            setupBarChart(stats.completedTasks)
            setupPieChart(stats.completedCount, stats.totalSnoozes)
        }
    }

    private fun buildHeadline(completed: Int, snoozes: Int, period: String): String {
        return when {
            completed == 0 -> "No tasks crushed this $period. Time to get serious!"
            snoozes == 0 -> "PERFECT! Crushed $completed tasks with ZERO snoozes!"
            else -> "Crushed $completed tasks, snoozed $snoozes times this $period"
        }
    }

    private fun setupBarChart(t
