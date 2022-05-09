package com.example.appparking.ui

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.RequiresApi

import com.example.appparking.Pruebas
import com.example.appparking.R
import com.example.appparking.databinding.ActivityUserMenuBinding
import com.example.appparking.functions.swap.SwapParking
import com.example.appparking.functions.pay.Pay
import com.example.appparking.location.LocationAcceder
import com.example.appparking.location.LocationGuardar
import com.example.appparking.location.LocationParking
import com.example.appparking.location.places.PlacesElectricChargers
import com.example.appparking.location.places.PlacesGasStation

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.custom_dialog_chrono.*

import kotlinx.android.synthetic.main.custom_dialog_chrono.view.*
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

    private lateinit var binding: ActivityUserMenuBinding
    private lateinit var baseDatos: FirebaseFirestore
    private lateinit var nombre: String
    private lateinit var ciudad: String
    private lateinit var marca: String

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var builder: Notification.Builder
    private val channelId = "1234"
    private val description = "Test notification"

    private var hashUserData: HashMap<String, Any> = hashMapOf()
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserMenuBinding.inflate(layoutInflater)
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
             * Si el usuario selecciona este ItemMenu será llevado a UserData para que realice los cambios que considere necesarios
             * @param nombre: Nombre recogido desde Firebase el cual podrá ser modificado por el usuario
             * @param ciudad: Ciudad recogida desde Firebase (mediante el uso de la clase Geocoder junto con la latitud y longitud aportadas) el cual podrá ser modificado por el usuario.
             * @param modelo: Modelo de vehículo del usuario recogido desde Firebase el cual podrá ser modificado por el usuario.
             * Estos 3 parámetros conformarán momentáneamente el perfil privado de cada Usuario el cual al iniciar la APP se identificará mediante un usuario y una contraseña.
             */
            cardHeader.setOnClickListener { startActivity(Intent(applicationContext, UserData::class.java)) ; transition() }

            /**
             * Cuando el usuario seleccione este ItemMenu será llevado automáticamente a LocationGuardar que consiste en un Maps Activity.
             * En dicha Activity el usuario podrá guardar la localización en tiempo real de su vehículo.
             * Incluso podrá mover el marker de la localización por si la ubicación mediante maps no es exacta del todo
             */
            itemLocationSave.setOnClickListener { startActivity(Intent(applicationContext, LocationGuardar::class.java)) ; transition() }

            /**
             * Cuando el usuario seleccione este ItemMenu será llevado automáticamente a LocationGuardar que consiste en un Maps Activity.
             * En dicha Activity el usuario podrá observar la ubicación de su vehículo.
             * De la misma manera se podrá hacer OnMarkerClick() para mostrarle al usuario la ruta exacta y más óptima (según Google Maps) hacia su vehículo.
             * Los siguientes parámetros se recogerán desde Firebase para ubicar el vehículo.
             * @param latitud
             * @param longitud
             */
            itemLocationLoad.setOnClickListener {
                if (latitud == 0.0 && longitud == 0.0)
                    toastPersonalizadoUserMenu1()
                else {
                    startActivity(Intent(applicationContext, LocationAcceder::class.java))
                    transition()
                }
            }

            /**
             * Si el usuario selecciona este ItemMenu será llevado a UserData para que realice los cambios que considere necesarios
             * @param nombre: Nombre recogido desde Firebase el cual podrá ser modificado por el usuario
             * @param ciudad: Ciudad recogida desde Firebase (mediante el uso de la clase Geocoder junto con la latitud y longitud aportadas) el cual podrá ser modificado por el usuario.
             * @param modelo: Modelo de vehículo del usuario recogido desde Firebase el cual podrá ser modificado por el usuario.
             * Estos 3 parámetros conformarán momentáneamente el perfil privado de cada Usuario el cual al iniciar la APP se identificará mediante un usuario y una contraseña.
             */
            itemUserData.setOnClickListener { startActivity(Intent(applicationContext, UserData::class.java)) ; transition() }

            /**
             * Cuando el usuario seleccione este Itemmenu será llevado automáticamente a LocationParking que consiste en un Maps Activity.
             * En dicha Activity se abrirá en un Fragment Google Maps con los parkings más cercanos marcados en Maps.
             */
            itemLocationParking.setOnClickListener { startActivity(Intent(applicationContext, LocationParking::class.java)) ; transition() }

            /**
             * Cuando el usuario seleccione este Itemmenu será llevado automáticamente a PlacesElectricChargers que consiste en un Maps Activity.
             * De nuevo este Activity consistirá en un Maps Fragment en el cual estrán marcados los cargadores eléctricos de vehículos cercanos.
             */
            itemElectrica.setOnClickListener { startActivity(Intent(this@UserMenu, PlacesElectricChargers::class.java)) ; transition() }

            /**
             * Cuando el usuario seleccione este Itemmenu será llevado automáticamente a PlacesGasStation que consiste en un Maps Activity.
             * De nuevo este Activity consistirá en un Maps Fragment en el cual estrán marcadas las gasolineras cercanas.
             */
            itemGasolinera.setOnClickListener { startActivity(Intent(this@UserMenu, PlacesGasStation::class.java)) ; transition() }

            /**
             * Cuando el usuario seleccione este ItemMenu se despliegará automáticamente un AlertDialog con un widget de TimePicker.
             * Con esto, el usuario seleccionará hasta que hora tiene la Zona Azul disponible y posteriormente comenzará una cuenta atrás en segundo plano.
             * 5 minutos antes de acabar la cuenta atrás se mandrá una notificación de que la Zona Azul estará a punto de terminar.
             */
            itemCronometro.setOnClickListener { showAlertView() }

            itemRewards.setOnClickListener { startActivity(Intent(applicationContext, Pruebas::class.java)) ; transition() }

            itemEstadistica.setOnClickListener { showNotification() }

            itemPay.setOnClickListener { startActivity(Intent(this@UserMenu, Pay::class.java)) ; transition() }

            itemSwapParking.setOnClickListener { startActivity(Intent(this@UserMenu, SwapParking::class.java)) ; transition() }

            itemContact.setOnClickListener { val intent = Intent(Intent.ACTION_DIAL) ; intent.data = Uri.parse(("tel:012345678")) ; startActivity(intent)}
        }
    }

    private fun transition() {
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out)
    }

    /**
     * AlertDialog mencionado anteriormente en Item Cronómetro
     */
    private fun showAlertView() {
        val dialogView =
            LayoutInflater.from(this@UserMenu).inflate(R.layout.custom_dialog_chrono, null)
        val builderDialogView = AlertDialog
            .Builder(this@UserMenu)
            .setView(dialogView)
        val alertDialog = builderDialogView.show()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        with(dialogView) {
            alertTimePicker.currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            alertTimePicker.setIs24HourView(true)

            cardAlertCancel.setOnClickListener {
                alertDialog.dismiss()
            }

            cardAlertSet.setOnClickListener {
                val countDownTimer = object : CountDownTimer(
                    getTimeNotification(
                        getCurrentTime()
                            .substring(0, 2),
                        getCurrentTime()
                            .substring(3),
                        alertTimePicker.hour.toString(),
                        alertTimePicker.minute.toString()
                    ).toLong() * 1000, 1000
                ) {
                    override fun onTick(p0: Long) {}
                    override fun onFinish() {
                        showNotification()
                    }
                }
                toastPersonalizadoUserMenu2()
                countDownTimer.start()
                alertDialog.dismiss()
            }
        }
    }

    /**
     * Notificación que se mostrará cuando el contador de la Zona Azul se termine.
     */
    @SuppressLint("RemoteViewLayout")
    private fun showNotification() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, LocationAcceder::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setContentTitle("Zona Azul")
                .setContentText("Zona azul finalizada")
                .setSmallIcon(R.drawable.iconoapp2)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.iconoapp2))
                .setContentIntent(pendingIntent)
        } else {
            builder = Notification.Builder(this)
                .setContentTitle("Code")
                .setContentText("Notification")
                .setSmallIcon(R.drawable.iconoapp2)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.iconoapp2))
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234, builder.build())
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("EEEE, HH:mm", Locale.getDefault()).format(Date())
    }
    private fun getCurrentTime(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    /**
     * Función hecha para calcular el tiempo que debe tardar en mandarse la notificación de la cuenta atrás iniciada desde ItemCronómetro.
     */
    private fun getTimeNotification(currentHour: String, currentMinute: String, userHour: String, userMinute: String): Int {
        val difference = arrayListOf<Int>()
        val hourDifference = userHour.toInt() - currentHour.toInt()
        var minuteDifference = userMinute.toInt() - currentMinute.toInt()
        when {
            minuteDifference < 0 -> {
                if (hourDifference == 1) {
                    minuteDifference += 60
                    difference.add(0)
                    difference.add(minuteDifference)
                } else {
                    difference.add(hourDifference)
                    difference.add(minuteDifference)
                }
            }
            minuteDifference == 0 -> {
                difference.add(hourDifference)
                difference.add(minuteDifference)
            }
            else -> {
                difference.add(hourDifference)
                difference.add(minuteDifference)
            }
        }
        return when (difference[0]) {
            0 -> {
                difference[1] * 60
            }
            else -> {
                ((difference[0] * 60) + difference[1]) * 60
            }
        }
    }

    /**
     * Función para cargar y posteriormente mostrar todos los datos de un Usuario desde Firebase.
     */
    @SuppressLint("SetTextI18n")
    private fun loadUserData() {
        baseDatos
            .collection("Usuarios")
            .document("Data")
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    result.getString("Latitud")?.toDouble().also {
                        if (it != null) {
                            latitud = it
                        }
                    }
                    result.getString("Longitud")?.toDouble().also {
                        if (it != null) {
                            longitud = it
                        }
                    }

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

                        marcaItemProfile.text = result.getString("MarcaTelefono")
                        modelItemProfile.text = result.getString("Modelo")
                    }
                }
            }
    }

    private fun toastPersonalizadoUserMenu1() {
        val layoutToast =  layoutInflater.inflate(R.layout.custom_toast_user_1, constraintToastMaps1)
        Toast(this).apply {
            duration = Toast.LENGTH_LONG
            setGravity(Gravity.BOTTOM, 0, 40)
            view = layoutToast
        }.show()
    }

    private fun toastPersonalizadoUserMenu2() {
        val layoutToast =  layoutInflater.inflate(R.layout.custom_toast_user_2, constraintToastMaps1)
        Toast(this).apply {
            duration = Toast.LENGTH_LONG
            setGravity(Gravity.BOTTOM, 0, 40)
            view = layoutToast
        }.show()    }
}