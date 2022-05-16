package com.example.appparking.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

import org.jetbrains.anko.alert

import timber.log.Timber

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue


class Bluetooth2: AppCompatActivity() {

    private lateinit var hash: HashMap<String, Any>
    private lateinit var binding: ActivityBluetoothBinding
    private lateinit var baseDatos: FirebaseFirestore
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private val deviceGattMap = ConcurrentHashMap<BluetoothDevice, BluetoothGatt>()
    private val operationQueue = ConcurrentLinkedQueue<BleOperationType>()
    private var pendingOperation: BleOperationType? = null

    private val connectionEventListener by lazy {
        ConnectionEventListener().apply {
            onDisconnect = {
                runOnUiThread {
                    alert {
                        title = "Disconnected"
                        message = "Disconnected or unable to connect to device."
                        positiveButton("OK") {}
                    }.show()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
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
            startActivity(Intent(this@Bluetooth2, UserMenu::class.java))
        }

        with(binding){
            recyclerBluetooth.adapter = adaptador
            recyclerBluetooth.layoutManager = LinearLayoutManager(this@Bluetooth2, LinearLayoutManager.VERTICAL, false)
        }

    }

    @SuppressLint("MissingPermission")
    private fun doNextOperation() {
        val operation = operationQueue.poll() ?: run {
            return
        }

        if (operation is Connect) {
            with(operation) {
                device.connectGatt(context, false, callback)
            }
            return
        }
    }

    fun teardownConnection(device: BluetoothDevice) {
        if (device.isConnected()) {
            enqueueOperation(Disconnect(device))
        } else {
            Timber.e("Not connected to ${device.address}, cannot teardown connection!")
        }
    }

    private val callback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val deviceAddress = gatt.device.address

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Timber.w("onConnectionStateChange: connected to $deviceAddress")
                    deviceGattMap[gatt.device] = gatt
                    Handler(Looper.getMainLooper()).post {
                        gatt.discoverServices()
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Timber.e("onConnectionStateChange: disconnected from $deviceAddress")
                    teardownConnection(gatt.device)
                }
            } else {
                Timber.e("onConnectionStateChange: status $status encountered for $deviceAddress!")
                Log.e("Prueba", "Prueba")

                if (pendingOperation is Connect) {
                    signalEndOfOperation()
                }
                teardownConnection(gatt.device)
            }
        }
    }

    @Synchronized
    private fun enqueueOperation(operation: BleOperationType) {
        operationQueue.add(operation)
        if (pendingOperation == null) {
            doNextOperation()
        }
    }

    @Synchronized
    private fun signalEndOfOperation() {
        Timber.d("End of $pendingOperation")
        pendingOperation = null
        if (operationQueue.isNotEmpty()) {
            doNextOperation()
        }
    }

    private fun BluetoothDevice.isConnected() = deviceGattMap.containsKey(this)

    private fun toastPersonalizadoBluetooth() {
        val layoutToast =  layoutInflater.inflate(R.layout.custom_toast_bluetooth, constraintToastMaps1)
        Toast(this).apply {
            duration = Toast.LENGTH_SHORT
            setGravity(Gravity.BOTTOM, 0, 40)
            view = layoutToast
        }.show()
    }

    override fun onResume() {
        super.onResume()
        ConnectionManager.registerListener(connectionEventListener)
        /**if (!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
        }*/
    }
}