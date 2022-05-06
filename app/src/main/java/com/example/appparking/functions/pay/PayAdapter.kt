package com.example.appparking.functions.pay

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appparking.R
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.activity_pay.*

class PayAdapter(private val listaTarjetas: ArrayList<PayCard>, private val clickListener: (PayCard) -> Unit): RecyclerView.Adapter<PayAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View, clickAtPosition: (String) -> Unit): RecyclerView.ViewHolder(itemView) {
        val idBanco: TextView = itemView.findViewById(R.id.textBankPay)
        val idTarjeta: TextView = itemView.findViewById(R.id.textNumberPay)
        val idPersona: TextView = itemView.findViewById(R.id.textNamePay)
        val caducidad: TextView = itemView.findViewById(R.id.textCaducidadPay)

        init {
            itemView.setOnClickListener {
                clickAtPosition(adapterPosition.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_card_pay, parent, false)
        ) {
            clickListener(listaTarjetas[it.toInt()])
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listaTarjetas[position]
        holder.idBanco.text = currentItem.idBanco
        holder.idTarjeta.text = currentItem.idTarjeta
        holder.idPersona.text = currentItem.idPersona
        holder.caducidad.text = currentItem.caducidad

        holder.itemView.setOnClickListener {
            clickListener(listaTarjetas[position])
        }
    }

    override fun getItemCount(): Int {
        return listaTarjetas.size
    }
}