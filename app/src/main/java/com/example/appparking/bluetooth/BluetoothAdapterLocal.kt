package com.example.appparking.bluetooth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appparking.R
import kotlinx.android.synthetic.main.activity_bluetooth.view.*

class BluetoothAdapterLocal (private val listaDispositivos: ArrayList<BluetoothCard>, private val clickListener: (BluetoothCard) -> Unit): RecyclerView.Adapter<BluetoothAdapterLocal.MyViewHolder>() {
    class MyViewHolder(itemView: View, clickAtPosition: (String) -> Unit): RecyclerView.ViewHolder(itemView) {
        var imageBluetoothDeviceLocal: ImageView = itemView.findViewById(R.id.imageBluetoothDeviceLocal)
        val bluetoothDeviceLocal: TextView = itemView.findViewById(R.id.bluetoothDeviceLocal)

        init {
            itemView.setOnClickListener {
                clickAtPosition(adapterPosition.toString())
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_bluetooth, parent, false)) {
            clickListener(listaDispositivos[it.toInt()])
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listaDispositivos[position]
        holder.imageBluetoothDeviceLocal.setImageResource(currentItem.imageBluetoothDeviceLocal)
        holder.bluetoothDeviceLocal.text = currentItem.bluetoothDeviceLocal

        holder.itemView.setOnClickListener {
            clickListener(listaDispositivos[position])
        }
    }

    override fun getItemCount(): Int {
        return listaDispositivos.size
    }


}