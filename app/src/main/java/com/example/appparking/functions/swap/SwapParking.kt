package com.example.appparking.functions.swap

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import android.os.Bundle

import android.widget.ArrayAdapter
import android.widget.Toast

import androidx.core.content.ContextCompat

import com.example.appparking.R
import com.example.appparking.databinding.ActivitySwapParkingBinding

import kotlin.random.Random

class SwapParking : AppCompatActivity() {

    private lateinit var binding: ActivitySwapParkingBinding
    private var chat: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySwapParkingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.card_background_darker)


        val itemMensajes = ArrayList<SwapCard>()

        for (i in 0..15) {
            if (i < 5) {
                itemMensajes.add(
                    SwapCard(
                        R.drawable.messages_1,
                        "Alex Maer",
                        "Hola Mundo",
                        "10:40 PM",
                        false,
                        R.id.newMessages,
                        Random.nextInt(1, 20).toString()
                    )
                )
                chat.add(itemMensajes[i].name)
            } else {
                itemMensajes.add(
                    SwapCard(
                        R.drawable.messages_1,
                        "Alex Ursa",
                        "Hola Mundo",
                        "10:40 PM",
                        true, R.id.newMessages,
                        Random.nextInt(1, 20).toString()
                    )
                )
                chat.add(itemMensajes[i].name)
            }
        }

        val adaptador = SwapAdapter(itemMensajes) {
            Toast.makeText(this, "Chat con ${it.name}", Toast.LENGTH_SHORT).show()
        }
        val adaptadorAutoComplete = ArrayAdapter(this, R.layout.spinner_item, chat)

        with(binding) {
            recyclerMessages.adapter = adaptador
            recyclerMessages.layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            autoCompleteMessages.setAdapter(adaptadorAutoComplete)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        transition()
    }

    private fun transition() {
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out)
    }
}