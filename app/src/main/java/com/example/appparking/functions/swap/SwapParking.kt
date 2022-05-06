package com.example.appparking.functions.swap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
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

        val itemMensajes = ArrayList<SwapCard>()

        for (i in 0..15){
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
            }
            else {
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

        val adaptador = SwapAdapter(itemMensajes){ Log.e("AAAA", "Pulsado") }
        val adaptadorAutoComplete = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, chat)

        with(binding){
            recyclerMessages.adapter = adaptador
            recyclerMessages.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            autoCompleteMessages.setAdapter(adaptadorAutoComplete)
        }
    }
}