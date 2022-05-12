package com.example.appparking.bluetooth

import android.annotation.SuppressLint
import android.app.Service.START_STICKY
import android.bluetooth.*
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.appparking.R
import com.example.appparking.databinding.ActivityBluetoothBinding
import com.example.appparking.ui.UserMenu

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import kotlinx.android.synthetic.main.custom_toast_maps_add_1.*
import android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED as ACTION_ACL_DISCONNECTED1

class Bluetooth : AppCompatActivity() {

    private lateinit var binding: ActivityBluetoothBinding
    private lateinit var baseDatos: FirebaseFirestore
    private lateinit var hash: HashMap<String, Any>
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBluetoothBinding.inflate(layoutInflater)
        baseDatos = Firebase.firestore
        setContentView(binding.root)

        if (intent.action.equals("iPhone de Alex")) {
            Log.e("Prueba", "Desconexión")
        }

        val first: IntentFilter = IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)
        val second: IntentFilter = IntentFilter(ACTION_ACL_DISCONNECTED1)

        this@Bluetooth.registerReceiver(broadcastReceiver2(), first)
        this@Bluetooth.registerReceiver(broadcastReceiver2(), second)

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

    override fun onBackPressed() {
        super.onBackPressed()
        transition()
    }

    @Override
    private fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothTurnedOnOff(), filter)
        return START_STICKY
    }

    inner class bluetoothTurnedOnOff: BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1!!.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                Log.e("Prueba", "Desconexión")
            }
        }
    }

    private fun transition() {
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out)
    }

    inner class broadcastReceiver: BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            Log.e("Prueba NORMAL", ACTION_ACL_DISCONNECTED1)
        }
    }

    inner class broadcastReceiver2: BroadcastReceiver() {
        @SuppressLint("MissingPermission", "HardwareIds")
        override fun onReceive(p0: Context?, p1: Intent?) {
            for (device in bluetoothAdapter.bondedDevices)
                Log.e("Devices", device.name.toString())

            binding.textBluetooth.append("Se ha guardao ubicación a las " + UserMenu().getCurrentTime() + "\n")
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
