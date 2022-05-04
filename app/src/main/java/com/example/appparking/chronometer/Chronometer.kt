package com.example.appparking.chronometer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.appparking.databinding.ActivityChronometerBinding

class Chronometer : AppCompatActivity() {

    private lateinit var binding: ActivityChronometerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChronometerBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}