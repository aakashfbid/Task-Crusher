package com.taskcrusher.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.taskcrusher.R
import com.taskcrusher.data.models.Task
import com.taskcrusher.data.models.UrgencyLevel
import com.taskcrusher.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val onComplete: (Task) -> Unit,
    private val onEdit: (Task) -> Unit,
    private val onDelete: (Task) -> Unit,
    private val isCompletedList: Boolean = false
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.tvTitle.text = task.title
            binding.tvDescription.text = task.description.ifEmpty { "" }
            binding.tvDescription.visibility = if (task.description.isEmpty()) View.GONE else View.VISIBLE

            if (task.hasAlarm && task.alarmTimeMillis > 0) {
                val sdf = SimpleDateFormat("EEE, MMM d h:mm a", Locale.getDefault())
                binding.tvAlarmTime.text = "alarm ${sdf.format(Date(task.alarmTimeMillis))}"
                binding.tvAlarmTime.visibility = View.VISIBLE
            } else {
                binding.tvAlarmTime.visibility = View.GONE
            }

            if (task.snoozeCount > 0) {
                binding.tvSnoozeCount.text = "Snoozed ${task.snoozeCount}x"
                binding.tvSnoozeCount.visibility = View.VISIBLE
            } else {
                binding.tvSnoozeCount.visibility = View.GONE
            }

            applyUrgencyTheme(task)

            if (isCompletedList) {
                binding.btnComplete.visibility = View.GONE
                binding.btnEdit.visibility = View.GONE
                binding.tvCompletedBadge.visibility = View.VISIBLE
            } else {
                binding.btnComplete.visibility = View.VISIBLE
                binding.btnEdit.visibility = View.VISIBLE
                binding.tvCompletedBadge.visibility = View.GONE
            }

            binding.btnComplete.setOnClickListener { onComplete(task) }
            binding.btnEdit.setOnClickListener { onEdit(task) }
            binding.btnDelete.setOnClickListener { onDelete(task) }

            if (task.getUrgencyLevel() == UrgencyLevel.CRITICAL) {
                val pulse = AnimationUtils.loadAnimation(binding.root.context, R.anim.pulse)
                binding.cardView.startAnimation(pulse)
            } else {
                binding.cardView.clearAnimation()
            }
        }

        private fun applyUrgencyTheme(task: Task) {
            val (strokeColor, textColor) = when (task.getUrgencyLevel()) {
                UrgencyLevel.CALM -> Pair(Color.parseColor("#00FFFF"), Color.parseColor("#00FFFF"))
                UrgencyLevel.WARNING -> Pair(Color.parseColor("#FF8C00"), Color.parseColor("#FF8C00"))
                UrgencyLevel.CRITICAL -> Pair(Color.parseColor("#DC143C"), Color.parseColor("#FF4444"))
            }
            binding.cardView.strokeColor = strokeColor
            binding.tvTitle.setTextColor(Color.WHITE)
            binding.tvAlarmTime.setTextColor(textColor)
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }
}
