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
import kotlinx.android.synthetic.main.item_ajustes_1.*
import kotlinx.android.synthetic.main.item_ajustes_2.*
import kotlinx.android.synthetic.main.item_ajustes_3.*
import kotlin.concurrent.thread

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

        binding.botonAceptar.setOnClickListener {
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
        if (editTextNombre.text.isEmpty() || editTextCiudad.text.isEmpty() || editTextMarca.text.isEmpty()) {
            Toast.makeText(this, "¡Has de rellenar todos los campos!", Toast.LENGTH_SHORT).show()
        } else {
            nombre = editTextNombre.text.toString()
            ciudad = editTextCiudad.text.toString()
            marca = editTextMarca.text.toString()

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

    private fun cargarAutoCompleteTextViewMarca() {
        val marcas = resources.getStringArray(R.array.marcascoche)
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_dropdown_item_1line, marcas)
        editTextMarca.setAdapter(adapter)
    }

    private fun cargarAutoCompleteTextViewProvincia() {
        val provincias = resources.getStringArray(R.array.provincias)
        val adapter = ArrayAdapter(this,
        android.R.layout.simple_dropdown_item_1line, provincias)
        editTextCiudad.setAdapter(adapter)
    }
}

