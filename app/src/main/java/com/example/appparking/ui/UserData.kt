package com.example.appparking.ui

import androidx.appcompat.app.AppCompatActivity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Toast

import com.example.appparking.R
import com.example.appparking.databinding.ActivityUserDataBinding

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase

import kotlinx.android.synthetic.main.activity_user_data.*
import kotlinx.android.synthetic.main.custom_toast_maps_add_1.*

class UserData : AppCompatActivity() {

    private lateinit var binding: ActivityUserDataBinding
    private lateinit var baseDatos: FirebaseFirestore

    private lateinit var nombre: String
    private lateinit var ciudad: String
    private lateinit var marca: String

    private var hashData: HashMap<String, Any> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        baseDatos = Firebase.firestore

        cargarAutoCompleteTextViewProvincia()

        cargarAutoCompleteTextViewMarca()

        loadData()

        binding.cardAccept.setOnClickListener {
            saveData()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        transition()
    }

    private fun transition() {
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out)
    }

    private fun loadData() {
        baseDatos = Firebase.firestore

        baseDatos
            .collection("Usuarios")
            .document("Data")
            .get()
            .addOnSuccessListener { result ->
                nombre = result.getField<String>("Nombre").toString()
                ciudad = result.getField<String>("Ciudad").toString()
                marca = result.getField<String>("Marca").toString()

                with(binding){
                    textoNombreAjustes.text = nombre
                    textoCiudadAjustes.text = ciudad
                    textoMarcaAjustes.text = marca
                }
            }
    }

    private fun saveData() {
        if (textDataNombre.text!!.isEmpty()) {
            toastPersonalizadoUserData1()
        } else {
            with(binding){
                nombre = textDataNombre.text.toString()
                ciudad = spinnerCities.selectedItem.toString()
                marca = spinnerModel.selectedItem.toString()
            }

            hashData["Ciudad"] = ciudad
            hashData["Marca"] = marca
            hashData["Nombre"] = nombre

            baseDatos
                .collection("Usuarios")
                .document("Data")
                .update(hashData)
                .addOnSuccessListener { Log.w(ContentValues.TAG, "¡Successfully written!") }
                .addOnFailureListener { Log.w(ContentValues.TAG, "Failed to be written!") }

            val volverMain = Intent(this, UserMenu::class.java)
            startActivity(volverMain)
        }
    }

    private fun cargarAutoCompleteTextViewProvincia() {
        val spinnerArrayAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, resources.getStringArray(R.array.provincias))

        with(binding){
            spinnerCities.adapter = spinnerArrayAdapter
            spinnerCities.setSelection(0)
        }
    }

    private fun cargarAutoCompleteTextViewMarca() {
        val spinnerArrayAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, resources.getStringArray(R.array.marcascoche))

        with(binding){
            spinnerModel.adapter = spinnerArrayAdapter
            spinnerModel.setSelection(0)
        }
    }

    private fun toastPersonalizadoUserData1() {
        val layoutToast =  layoutInflater.inflate(R.layout.custom_toast_user_data_1, constraintToastMaps1)
        Toast(this).apply {
            duration = Toast.LENGTH_LONG
            setGravity(Gravity.BOTTOM, 0, 40)
            view = layoutToast
        }.show()
    }
}