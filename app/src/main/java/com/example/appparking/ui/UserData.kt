package com.example.appparking.ui

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.appparking.R
import com.example.appparking.databinding.ActivityUserDataBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_user_data.*
import kotlinx.android.synthetic.main.activity_user_menu.*
import kotlinx.android.synthetic.main.item_ajustes_1.*
import kotlinx.android.synthetic.main.item_ajustes_2.*
import kotlinx.android.synthetic.main.item_ajustes_3.*

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
            Toast.makeText(this, "¡Has de rellenar todos los campos!", Toast.LENGTH_SHORT).show()
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
        val spinnerArrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.provincias))
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        with(binding){
            spinnerCities.adapter = spinnerArrayAdapter
            spinnerCities.setSelection(0)
        }
    }

    private fun cargarAutoCompleteTextViewMarca() {
        val spinnerArrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.marcascoche))
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        with(binding){
            spinnerModel.adapter = spinnerArrayAdapter
            spinnerModel.setSelection(0)
        }
    }
}

