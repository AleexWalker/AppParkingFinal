package com.example.appparking

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import com.example.appparking.ui.UserMenu
import kotlinx.android.synthetic.main.activity_pruebas.*

class Pruebas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pruebas)

        textViewPrueba.text = cargarNombre()

        buttonPrueba.setOnClickListener {

            if (cargarNombre() == null) {
                guardarNombre()
            } else {
                guardarNombre()
                cargarNombre()
                intent = Intent(this, UserMenu::class.java)
                startActivity(intent)
            }
        }
    }

    private fun cargarNombre() : String? {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("pruebas", Context.MODE_PRIVATE)

        return sharedPreferences.getString("nombre", null)
    }

    private fun guardarNombre() {
        val nombre: Editable? = editTextTextPrueba.text
        textViewPrueba.text = nombre
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("pruebas", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.apply {
            putString("nombre", nombre.toString())
        }.apply()
    }
}