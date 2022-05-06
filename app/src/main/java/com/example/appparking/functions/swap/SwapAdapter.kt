package com.example.appparking.functions.swap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appparking.R
import com.google.android.material.card.MaterialCardView

class SwapAdapter (private val listaMensajes: ArrayList<SwapCard>, private val clickListener: (SwapCard) -> Unit): RecyclerView.Adapter<SwapAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View, clickAtPosition: (String) -> Unit): RecyclerView.ViewHolder(itemView) {
        var profilePhoto: ImageView = itemView.findViewById(R.id.profilePhoto)
        val name: TextView = itemView.findViewById(R.id.nameMessager)
        val message: TextView = itemView.findViewById(R.id.lastMessage)
        val hourLastMessage: TextView = itemView.findViewById(R.id.hourLastMessage)
        var readedCheck = false
        var readed: MaterialCardView = itemView.findViewById(R.id.newMessages)
        val countReadedMessages: TextView = itemView.findViewById(R.id.countReadedMessages)

        init {
            itemView.setOnClickListener {
                clickAtPosition(adapterPosition.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        ) {
            clickListener(listaMensajes[it.toInt()])
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listaMensajes[position]
        holder.profilePhoto.setImageResource(currentItem.profilePhoto)
        holder.name.text = currentItem.name
        holder.message.text = currentItem.message
        holder.hourLastMessage.text = currentItem.hourLastMessage
        holder.readedCheck = currentItem.readedCheck

        if (!currentItem.readedCheck)
            holder.readed.visibility = View.VISIBLE
        else
            holder.readed.visibility = View.INVISIBLE

        holder.countReadedMessages.text = currentItem.readedMessages
        holder.itemView.setOnClickListener {
            clickListener(listaMensajes[position])
        }    }

    override fun getItemCount(): Int {
        return listaMensajes.size
    }
}