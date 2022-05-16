package com.example.appparking.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BluetoothBroadcastReceiver: BroadcastReceiver() {
    @SuppressLint("LogNotTimber", "MissingPermission")
    override fun onReceive(p0: Context?, p1: Intent?) {

        Log.e("BluetoothReceiver", "${p1!!.action}")

        when( p1.action ) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                val device : BluetoothDevice = p1.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                Log.e("BluetoothReceiver", "BluetoothDevice ${device.name} connected")
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                val device : BluetoothDevice = p1.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                Log.e("BluetoothReceiver", "BluetoothDevice ${device.name} disconnected")
            }
        }
    }
}