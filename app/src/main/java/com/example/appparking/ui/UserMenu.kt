package com.example.appparking.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.appparking.Pruebas
import com.example.appparking.R
import com.example.appparking.carlocation.LocationAcceder
import com.example.appparking.carlocation.LocationGuardar
import com.example.appparking.carlocation.LocationParking
import com.example.appparking.databinding.ActivityUserMenuBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_user_menu.*
import kotlinx.android.synthetic.main.item_1.*
import kotlinx.android.synthetic.main.item_2.*
import kotlinx.android.synthetic.main.item_3.*
import kotlinx.android.synthetic.main.item_4.*
import kotlinx.android.synthetic.main.item_5.*
import java.text.SimpleDateFormat
import java.util.*

class UserMenu : AppCompatActivity() {

    private lateinit var binding: ActivityUserMenuBinding
    private lateinit var baseDatos: FirebaseFirestore
    private var nombre = String()
    private var ciudad = String()
    private var marca = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        baseDatos = Firebase.firestore
        baseDatos.firestoreSettings = FirebaseFirestoreSettings.Builder().build()

        cargarDatosUsuario()

        /**
         * Lanzamos la activity_datos_usuario para que nuestro cliente realice los cambios que considere necesarios
         */
        binding.cardHeader.setOnClickListener {
            val lanzarDatosUsuario = Intent(this, UserData::class.java)
            startActivity(lanzarDatosUsuario)
        }

        /**
         * Recogemos los datos de la Latitud y Longitud enviados desde la clase de GoogleMaps
         * @param latitudGuardada : Parámetro de tipo Double que guarda la Latiud
         * @param longitudGuardada : Parámetro de tipo Double que guarda la Longitud
         */
        var latitudGuardada = obtenerDatosLatitud()
        var longitudGuardada = obtenerDatosLongitud()

        if (latitudGuardada != null) {
            if (longitudGuardada != null) {
                guardarCoordenadas(latitudGuardada, longitudGuardada)
            }
        }

        /**
         * En cuanto el usuario interactue con dicho layout lo mandamos a la clase de GoogleMaps
         * @param layoutPrimero : Parámetro que hace referencia al Layout item_1 insertado en el activity_main.xml
         */
        binding.primerItem.cardGuardarLocalizacion.setOnClickListener {
            startActivity(Intent(this, LocationGuardar::class.java))
        }

        /**
         * En cuanto el usuario interactue con dicho layout lo mandamos a la clase de GoogleMaps
         * @param layoutSegundo : Parámetro que hace referencia al Layout item_2 insertado en el activity_main.xml
         */
        binding.segundoItem.cardAccederLocalizacion.setOnClickListener {
            if (latitudGuardada == null || longitudGuardada == null) {
                Toast.makeText(this, "¡Primero has de guardar tu ubicación actual!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, LocationAcceder::class.java)
                if (latitudGuardada != null) {
                    intent.putExtra("latitud", latitudGuardada)
                }
                if (longitudGuardada != null) {
                    intent.putExtra("longitud", longitudGuardada)
                }
                startActivity(intent)
            }
        }

        /**
         * En cuanto el usuario interactue con dicho layout lo mandamos a la clase de Parking
         * @param layoutTercero : Parámetro que hace referencia al Layout item_3 insertado en el activity_main.xml
         */
        binding.tercerItem.cardParkingsCercanos.setOnClickListener {
            startActivity(Intent(this, LocationParking::class.java))
        }

        /**
         * En cuanto el usuario interactue con dicho layout lo mandamos a la clase de Pruebas
         * @param layoutTercero : Parámetro que hace referencia al Layout item_3 insertado en el activity_main.xml
         */
        binding.cuartoItem.cardMenuAmpliacion1.setOnClickListener {
            startActivity(Intent(this, Pruebas::class.java))
        }

        binding.quintoItem.cardMenuAmpliacion2.setOnClickListener {

        }

        añadirHorasLayouts()
    }

    private fun guardarCoordenadas(latitudGuardada : Double, longitudGuardada : Double) {
        val sharedPreferences : SharedPreferences = getSharedPreferences("coordenadasLatLng", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.apply {
            putString("latitud", latitudGuardada.toString())
            putString("longitud", longitudGuardada.toString())
        }.apply()
    }

    private fun añadirHorasLayouts() {
        textTime_1.text = getCurrentTime()
        textTime_2.text = getCurrentTime()
        textTime_3.text = getCurrentTime()
        textTime_4.text = getCurrentTime()
        textTime_5.text = getCurrentTime()
    }

    private fun obtenerDatosLatitud(): Double? {
        val bundle = intent.extras
        return bundle?.getDouble("latitud")
    }

    private fun obtenerDatosLongitud(): Double? {
        val bundle = intent.extras
        return bundle?.getDouble("longitud")
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("EEEE, HH:mm", Locale.getDefault()).format(Date())
    }

    private fun cargarDatosUsuario() {
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
                binding.textCiudad.text = ciudad
                binding.textMarca.text = marca
                binding.textNombre.text = nombre
            }
    }
}