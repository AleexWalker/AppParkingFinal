package com.example.appparking.ui

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import com.example.appparking.R
import com.example.appparking.databinding.SettingsActivityBinding

class UserSettings : AppCompatActivity() {

    private lateinit var binding: SettingsActivityBinding

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadSwitchesChecked()

        with(binding){

            back.setOnClickListener {
                finish()
                startActivity(Intent(this@UserSettings, UserMenu::class.java))
            }

            switchNotification.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    switchNotification.isChecked = true
                    saveSwitchStateConnected("1")
                } else {
                    switchNotification.isChecked = false
                    saveSwitchStateDisconnected("1")
                }
            }

            switchSounds.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    NotificationManager.IMPORTANCE_HIGH
                    switchSounds.isChecked = true
                    saveSwitchStateConnected("2")
                } else {
                    NotificationManager.IMPORTANCE_LOW
                    switchSounds.isChecked = false
                    saveSwitchStateDisconnected("2")
                }
            }

            switchVibration.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    switchVibration.isChecked = true
                    saveSwitchStateConnected("3")
                } else {
                    switchVibration.isChecked = false
                    saveSwitchStateDisconnected("3")
                }
            }
        }
    }

    private fun loadSwitchesChecked() {
        binding.switchNotification.isChecked = false
        binding.switchSounds.isChecked = false
        binding.switchVibration.isChecked = false
        for (i in 0 until loadSwitchState().size) {
            if (loadSwitchState()[i] == "checked") {
                when (i) {
                    0 -> {
                        binding.switchNotification.isChecked = true
                        Log.e("Checked1", "Checked1")
                    }
                    1 -> {
                        binding.switchSounds.isChecked = true
                        Log.e("Checked1", "Checked1")
                    }
                    2 -> {
                        binding.switchVibration.isChecked = true
                        Log.e("Checked1", "Checked1")
                    }
                }
            }
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        transition()
    }

    private fun loadSwitchState(): ArrayList<String> {
        val preferences: SharedPreferences = getSharedPreferences("switch", MODE_PRIVATE)
        val states = arrayListOf<String>()
        for (i in 1..3) {
            states.add(preferences.getString("state$i", "default").toString())
        }
        return states
    }

    private fun saveSwitchStateConnected(num: String) {
        val switchEditor: SharedPreferences.Editor =
            getSharedPreferences("switch", MODE_PRIVATE).edit()
        switchEditor.putString("state$num", "checked")
        switchEditor.apply()
    }

    private fun saveSwitchStateDisconnected(num: String) {
        val switchEditor: SharedPreferences.Editor =
            getSharedPreferences("switch", MODE_PRIVATE).edit()
        switchEditor.putString("state$num", "unchecked")
        switchEditor.apply()
    }

    private fun transition() {
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out)
    }
}