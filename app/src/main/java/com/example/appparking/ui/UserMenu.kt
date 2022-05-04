package com.example.appparking.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast

import com.example.appparking.Pruebas
import com.example.appparking.R
import com.example.appparking.chronometer.Chronometer
import com.example.appparking.location.LocationAcceder
import com.example.appparking.location.LocationGuardar
import com.example.appparking.location.LocationParking
import com.example.appparking.databinding.ActivityUserMenuExpBinding

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import kotlinx.android.synthetic.main.activity_user_menu.*
import kotlinx.android.synthetic.main.activity_user_menu_exp.view.*
import kotlinx.android.synthetic.main.custom_toast_maps_add_1.*
import kotlinx.android.synthetic.main.item_1.*
import kotlinx.android.synthetic.main.item_2.*
import kotlinx.android.synthetic.main.item_3.*
import kotlinx.android.synthetic.main.item_4.*
import kotlinx.android.synthetic.main.item_5.*

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class UserMenu : AppCompatActivity() {

    private lateinit var binding: ActivityUserMenuExpBinding
    private lateinit var baseDatos: FirebaseFirestore
    private lateinit var nombre: String
    private lateinit var ciudad: String
    private lateinit var marca: String

    private var hashUserData: HashMap<String, Any> = hashMapOf()
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserMenuExpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        baseDatos = Firebase.firestore
        baseDatos.firestoreSettings = FirebaseFirestoreSettings.Builder().build()

        /**
         * Recogemos todos los datos desde Firebase posteriormente enviados desde la clase de GoogleMaps
         * @param latitud: Parámetro de tipo Double que guarda la Latiud
         * @param longitud: Parámetro de tipo Double que guarda la Longitud
         */
        loadUserData()

        with(binding) {
            /**
             * Lanzamos la activity_user_data para que nuestro cliente realice los cambios que considere necesarios
             */
            cardHeader.setOnClickListener {
                startActivity(Intent(applicationContext, UserData::class.java))
            }

            /**
             * En cuanto el usuario interactue con dicho layout lo mandamos a la clase de LocationGuardar
             * @param layoutPrimero : Parámetro que hace referencia al Layout item_1 insertado en el activity_main.xml
             */
            itemLocationSave.setOnClickListener {
                startActivity(Intent(applicationContext, LocationGuardar::class.java))
            }

            /**
             * En cuanto el usuario interactue con dicho layout lo mandamos a la clase de LocationGuardar
             * @param layoutPrimero : Parámetro que hace referencia al Layout item_1 insertado en el activity_main.xml
             */
            itemLocationLoad.setOnClickListener {
                if (latitud == 0.0 && longitud == 0.0)
                    toastPersonalizadoUserMenu1()
                else
                    startActivity(Intent(applicationContext, LocationAcceder::class.java))
            }

            itemUserData.setOnClickListener {
                startActivity(Intent(applicationContext, UserData::class.java))
            }

            /**
             * En cuanto el usuario interactue con dicho layout lo mandamos a la clase de Parking
             * @param layoutTercero : Parámetro que hace referencia al Layout item_3 insertado en el activity_main.xml
             */
            itemLocationParkings.setOnClickListener {
                startActivity(Intent(applicationContext, LocationParking::class.java))
            }

            itemCronometro.setOnClickListener {
                val dialogView = LayoutInflater.from(applicationContext).inflate(R.layout.custom_dialog_chrono, null)
                val builderDialogView = android.app.AlertDialog.Builder(applicationContext)
                    .setView(dialogView)
                val alertDialog = builderDialogView.show()
                alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            itemRewards.setOnClickListener {
                startActivity(Intent(applicationContext, Pruebas::class.java))
            }

            itemPay.setOnClickListener {}

            itemSwapParking.setOnClickListener {}

            itemContact.setOnClickListener {}
        }
    }

    private fun toastPersonalizadoUserMenu1() {
        val layoutToast =  layoutInflater.inflate(R.layout.custom_toast_user_1, constraintToastMaps1)
        Toast(this).apply {
            duration = Toast.LENGTH_SHORT
            setGravity(Gravity.TOP, 0, 20)
            view = layoutToast
        }.show()    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("EEEE, HH:mm", Locale.getDefault()).format(Date())
    }

    @SuppressLint("SetTextI18n")
    private fun loadUserData() {
        baseDatos
            .collection("Usuarios")
            .document("Data")
            .get()
            .addOnSuccessListener { result ->
                latitud = result.getString("Latitud")!!.toDouble()
                longitud = result.getString("Longitud")!!.toDouble()
                nombre = result.getString("Nombre").toString()
                ciudad = result.getString("Ciudad").toString()
                marca = result.getString("Marca").toString()

                with(binding){
                    textNombre.text = nombre
                    textCiudad.text = ciudad
                    textMarca.text = marca

                    textLocationSaveDay.text = result.getString("Hora").toString().uppercase()
                    textLocationLoadStreet.text = "${result.getString("Calle")}, ${result.getString("Numero")}"
                    textLocationParkingData.text = "${result.getString("Localidad")}"
                }
            }
    }
}