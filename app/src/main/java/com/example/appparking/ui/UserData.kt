package com.example.appparking.ui

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.appparking.R
import com.example.appparking.databinding.ActivityUserDataBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.item_ajustes_1.*
import kotlinx.android.synthetic.main.item_ajustes_2.*
import kotlinx.android.synthetic.main.item_ajustes_3.*
import java.util.HashSet

class UserData : AppCompatActivity() {

    private lateinit var binding: ActivityUserDataBinding
    private lateinit var baseDatos: FirebaseFirestore

    private var hashData: HashMap<String, Any> = hashMapOf()
    private var nombre = String()
    private var ciudad = String()
    private var marca = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        baseDatos = Firebase.firestore

        cargarAutoCompleteTextViewProvincia()
        cargarAutoCompleteTextViewMarca()
        cargarDatosUsuario()

        binding.botonAceptar.setOnClickListener {
            if (editTextNombre.text.isEmpty() || editTextCiudad.text.isEmpty() || editTextMarca.text.isEmpty()) {
                Toast.makeText(this, "¡Has de rellenar todos los campos!", Toast.LENGTH_SHORT).show()
            } else {
                nombre = editTextNombre.text.toString()
                ciudad = editTextCiudad.text.toString()
                marca = editTextMarca.text.toString()

                hashData["CiudadPerfil"] = ciudad
                hashData["MarcaPerfil"] = marca
                hashData["NombrePerfil"] = nombre

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

    private fun cargarDatosUsuario() {
        /**
         * Parámetros de main_activity
         * @param textNombreAjustes : TextView
         * @param textCiudadAjustes : TextView
         * @param textMarcaAjustes : TextView
         */
        baseDatos
            .collection("Usuarios")
            .document("Data")
            .get()
            .addOnSuccessListener { data ->
                if (data != null) {
                    ciudad = data.getString("CiudadPerfil").toString()
                    marca = data.getString("MarcaPerfil").toString()
                    nombre = data.getString("NombrePerfil").toString()
                }
                binding.textoCiudadAjustes.text = ciudad
                binding.textoMarcaAjustes.text = marca
                binding.textoNombreAjustes.text = nombre
            }


    }
}

