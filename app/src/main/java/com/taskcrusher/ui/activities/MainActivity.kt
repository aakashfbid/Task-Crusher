package com.taskcrusher.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.taskcrusher.R
import com.taskcrusher.databinding.ActivityMainBinding
import com.taskcrusher.service.AlarmService
import com.taskcrusher.ui.TaskViewModel
import com.taskcrusher.ui.adapters.TaskAdapter
import com.taskcrusher.utils.AlarmScheduler

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var activeAdapter: TaskAdapter
    private lateinit var completedAdapter: TaskAdapter

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AlarmService.createNotificationChannels(this)
        requestPermissions()
        setupUI()
        observeData()
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!AlarmScheduler.canScheduleExactAlarms(this)) {
                Snackbar.make(binding.root, "Please allow exact alarms for task reminders", Snackbar.LENGTH_LONG)
                    .setAction("Allow") {
                        startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = Uri.parse("package:$packageName")
                        })
                    }.show()
            }
        }
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)

        binding.fabAddTask.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> showActiveList()
                    1 -> showCompletedList()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        activeAdapter = TaskAdapter(
            onComplete = { task -> viewModel.markComplete(task.id) },
            onEdit = { task ->
                val intent = Intent(this, AddTaskActivity::class.java)
                intent.putExtra("task_id", task.id)
                startActivity(intent)
            },
            onDelete = { task -> viewModel.deleteTask(task) }
        )

        completedAdapter = TaskAdapter(
            onComplete = {},
            onEdit = {},
            onDelete = { task -> viewModel.deleteTask(tas
