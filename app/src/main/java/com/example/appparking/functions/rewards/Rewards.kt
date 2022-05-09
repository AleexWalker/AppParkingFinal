package com.example.appparking.functions.rewards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appparking.R
import com.example.appparking.databinding.ActivityRewardsBinding
import kotlin.random.Random

class Rewards : AppCompatActivity() {

    private lateinit var binding: ActivityRewardsBinding
    private var transactions: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.card_background_darker)

        val itemRewards = ArrayList<RewardCard>()

        for (i in 0..20) {
            itemRewards.add(
                RewardCard(
                    203,
                    Random.nextInt(-20, 20),
                    "122683",
                    "04/03/1999")
            )
            transactions.add(itemRewards[i].transaction)
        }

        val adaptador = RewardAdapter(itemRewards){
            Toast.makeText(this, "Transacción número: ${it.transaction}", Toast.LENGTH_SHORT).show()
        }
        val adaptadorAutoComplete = ArrayAdapter(this, R.layout.spinner_item, transactions)

        with(binding){
            recyclerRewards.adapter = adaptador
            recyclerRewards.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

            autoCompleteRewards.setAdapter(adaptadorAutoComplete)
        }
    }
}