package com.example.appparking.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appparking.R
import com.example.appparking.databinding.ActivityBluetoothBinding
import com.example.appparking.ui.UserMenu
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.custom_toast_maps_add_1.*

class Bluetooth : AppCompatActivity() {
    private lateinit var binding: ActivityBluetoothBinding
    private lateinit var hash: HashMap<String, Any>
    private lateinit var baseDatos: FirebaseFirestore

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    @SuppressLint("MissingPermission", "LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBluetoothBinding.inflate(layoutInflater)
        baseDatos = Firebase.firestore
        setContentView(binding.root)

        val itemDispositivos = ArrayList<BluetoothCard>()

        for(device in bluetoothAdapter.bondedDevices){
            itemDispositivos.add(BluetoothCard(R.drawable.menu_bluetooth, device.name.toString()))
        }

        val adaptador = BluetoothAdapterLocal(itemDispositivos){
            hash = hashMapOf()
            hash["Dispositivo"] = it.bluetoothDeviceLocal
            baseDatos
                .collection("Usuarios")
                .document("Data")
                .update(hash)
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Successfly written")
                    toastPersonalizadoBluetooth()
                }
                .addOnFailureListener {
                    Log.w(ContentValues.TAG, "Failed to be written!")
                }
            startActivity(Intent(this@Bluetooth, UserMenu::class.java))
        }

        with(binding){
            recyclerBluetooth.adapter = adaptador
            recyclerBluetooth.layoutManager = LinearLayoutManager(this@Bluetooth, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun toastPersonalizadoBluetooth() {
        val layoutToast =  layoutInflater.inflate(R.layout.custom_toast_bluetooth, constraintToastMaps1)
        Toast(this).apply {
            duration = Toast.LENGTH_SHORT
            setGravity(Gravity.BOTTOM, 0, 40)
            view = layoutToast
        }.show()
    }
}