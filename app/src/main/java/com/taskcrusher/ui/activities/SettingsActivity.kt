package com.taskcrusher.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.taskcrusher.R
import com.taskcrusher.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"

        val prefs = getSharedPreferences("taskcrusher_prefs", MODE_PRIVATE)

        val currentMode = prefs.getString("personality_mode", "hype_man")
        if (currentMode == "roaster") {
            binding.rbRoaster.isChecked = true
        } else {
            binding.rbHypeMan.isChecked = true
        }

        binding.rgPersonality.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.rbRoaster -> "roaster"
                else -> "hype_man"
            }
            prefs.edit().putString("personality_mode", mode).apply()
            val label = if (mode == "roaster") "Roaster mode activated!" else "Hype Man mode activated!"
            Toast.makeText(this, label, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
