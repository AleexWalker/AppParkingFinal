package com.example.appparking.functions.rewards

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.appparking.R
import kotlin.coroutines.coroutineContext

class RewardAdapter (private val listaRewards: ArrayList<RewardCard>, private val clickListener: (RewardCard) -> Unit): RecyclerView.Adapter<RewardAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View, clickAtPosition: (String) -> Unit): RecyclerView.ViewHolder(itemView) {
        var totalCardano: TextView = itemView.findViewById(R.id.totalCoin)
        val rewardCardano: TextView = itemView.findViewById(R.id.marketCoin)
        val transactionCardano: TextView = itemView.findViewById(R.id.transactionCoin)
        val dateCardano: TextView = itemView.findViewById(R.id.dateTransactionCoin)

        init {
            itemView.setOnClickListener {
                clickAtPosition(adapterPosition.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rewards, parent, false)
        ) {
            clickListener(listaRewards[it.toInt()])
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listaRewards[position]
        holder.totalCardano.text = currentItem.total.toString()
        holder.rewardCardano.text = currentItem.reward.toString()
        holder.transactionCardano.text = currentItem.transaction
        holder.dateCardano.text = currentItem.date

        when {
            currentItem.reward > 0 -> holder.rewardCardano.setTextColor(Color.parseColor("#43a047"))
            currentItem.reward < 0 -> holder.rewardCardano.setTextColor(Color.parseColor("#ED3B3B"))
            else -> holder.rewardCardano.setTextColor(Color.parseColor("#FFFFFF"))
        }

        holder.itemView.setOnClickListener {
            clickListener(listaRewards[position])
        }
    }

    override fun getItemCount(): Int {
        return listaRewards.size
    }
}