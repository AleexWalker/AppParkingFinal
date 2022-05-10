package com.example.appparking.functions.pay

import android.content.ContentValues.TAG
import android.graphics.Color
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appparking.databinding.ActivityPayBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import kotlin.math.absoluteValue

class Pay: AppCompatActivity() {

    private lateinit var binding: ActivityPayBinding
    private lateinit var baseDatos: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        baseDatos = Firebase.firestore

        loadTarjetas()
        loadUserData()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        transition()
    }

    private fun transition() {
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out)
    }

    private fun loadTarjetas() {
        val itemTarjeta = ArrayList<PayCard>()
        baseDatos
            .collection("Tarjetas")
            .get()
            .addOnSuccessListener { tarjeta ->
                try {
                    if (tarjeta != null) {
                        for (document in tarjeta) {
                            Log.e("AAAAAAAA", document.getString("IDBanco").toString())
                            itemTarjeta.add(PayCard(
                                document
                                    .getString("IDBanco").toString(),
                                document
                                    .getString("IDTarjeta").toString(),
                                document
                                    .getString("NombrePersona").toString(),
                                document
                                    .getString("Caducidad").toString()))
                        }
                        loadAdapter(itemTarjeta)
                    }
                } catch (e : Exception) {
                    e.message?.let { Log.e(TAG, it) }
                }
            }.addOnFailureListener {
                    e -> Log.e(TAG, "Error writing the document", e)
            }
    }

    private fun loadAdapter(itemTarjeta: ArrayList<PayCard>) {
        val adaptador = PayAdapter(itemTarjeta) { Toast.makeText(this, "Tarjeta de ${it.idBanco} seleccionada", Toast.LENGTH_LONG).show() }

        with(binding) {
            recyclerCardsPay.adapter = adaptador
            recyclerCardsPay.setHasFixedSize(true)
            recyclerCardsPay.itemAnimator = DefaultItemAnimator()
            recyclerCardsPay.layoutManager = LinearLayoutManager(this@Pay, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun loadUserData() {
        baseDatos
            .collection("Usuarios")
            .document("Data")
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    with(binding){
                        textNombre.text = result.getString("Nombre").toString()
                        textCiudad.text = result.getString("Ciudad").toString()
                        textMarca.text = result.getString("Marca").toString()
                    }
                }
            }
    }
}