package com.example.appparking.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.example.appparking.databinding.ActivityBluetoothBinding

class Bluetooth : AppCompatActivity() {
    private lateinit var binding: ActivityBluetoothBinding

    @SuppressLint("MissingPermission", "LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBluetoothBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}