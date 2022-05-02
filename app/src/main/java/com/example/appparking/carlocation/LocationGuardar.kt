package com.example.appparking.carlocation

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.appparking.ui.UserMenu
import com.example.appparking.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.appparking.databinding.ActivityLocationGuardarBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.custom_toast_maps_add_1.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class LocationGuardar : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMyLocationButtonClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityLocationGuardarBinding
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var baseDatos: FirebaseFirestore
    private lateinit var geocoder: Geocoder

    private var hashData: HashMap<String, Any> = hashMapOf()
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0

    companion object {
        const val LOCATION_REQUEST_CODE = 1
    }

    data class Coche(val latitud: Double, val longitud: Double, val fecha: String, val hora: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationGuardarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Firestore Database & Load Scanner
        baseDatos = Firebase.firestore

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment
            .getMapAsync(this)

        geocoder = Geocoder(applicationContext, Locale.getDefault())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try {
            val succes : Boolean = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style_dark
                ))
            if (!succes) {
                Log.e("MapsActivity", "Style pairsing failed")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsActivity", "Can't find map style. Error: ", e)
        }

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        mMap.isTrafficEnabled = true

        mMap.setOnMarkerDragListener(this)
        mMap.setOnMyLocationButtonClickListener(this)

        binding.itemBotonAddMapsType.imagenMapsType.setOnClickListener {
            if (mMap.mapType == GoogleMap.MAP_TYPE_NORMAL)
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            else
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }

        binding.itemBotonAddMapsBack.imagenMapsBack.setOnClickListener {
            startActivity(Intent(this, UserMenu::class.java))
        }

        binding.guardarLocalizacion.cardGuardarLocalizacion.setOnClickListener {
            saveLocation(Coche(latitud, longitud, getCurrentDate(), getCurrentTime()))
            startActivity(Intent(this, UserMenu::class.java))
        }

        setUpMap()
    }

    override fun onMyLocationButtonClick(): Boolean {
        mMap
            .animateCamera(CameraUpdateFactory
                .newLatLngZoom(LatLng(latitud, longitud), 18f))
        return true
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        p0.showInfoWindow()
        return true
    }

    override fun onMarkerDrag(p0: Marker) {
        return
    }

    override fun onMarkerDragEnd(p0: Marker) {
        latitud = p0.position.latitude
        longitud = p0.position.longitude

        toastPersonalizadoMaps1()
    }

    override fun onMarkerDragStart(p0: Marker) {return}

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap("Coche", currentLatLong)

                mMap
                    .animateCamera(CameraUpdateFactory
                        .newLatLngZoom(currentLatLong, 18f))

                latitud = location.latitude
                longitud = location.longitude
            }
        }
    }

    private fun saveLocation(coche: Coche) {
        val listGeocoder: List<Address> = geocoder.getFromLocation(latitud, longitud, 1)

        hashData["Latitud"] = coche.latitud.toString()
        hashData["Longitud"] = coche.longitud.toString()
        hashData["Fecha"] = coche.fecha
        hashData["Hora"] = coche.hora

        hashData["Localidad"] = listGeocoder[0].locality
        hashData["Localidad"] = listGeocoder[0].locality
        hashData["Numero"] = listGeocoder[0].featureName
        hashData["Ciudad"] = listGeocoder[0].subAdminArea
        hashData["Comunidad"] = listGeocoder[0].adminArea
        hashData["Pais"] = listGeocoder[0].countryName
        hashData["Calle"] = listGeocoder[0].thoroughfare
        hashData["Codigo Postal"] = listGeocoder[0].postalCode

        baseDatos
            .collection("Usuarios")
            .document("Data")
            .update(hashData)
            .addOnSuccessListener { Log.d(ContentValues.TAG, "Successfly written") }
            .addOnFailureListener { Log.w(ContentValues.TAG, "Failed to be written!") }

    }

    private fun placeMarkerOnMap(title: String, currentLatLong: LatLng) {
        val marker: Marker? = mMap.addMarker(MarkerOptions()
            .title(title)
            .flat(false)
            .position(currentLatLong)
            .draggable(true)
            .icon(defaultMarker(HUE_AZURE)))
        marker!!.showInfoWindow()
    }

    private fun toastPersonalizadoMaps1() {
        val layoutToast =  layoutInflater.inflate(R.layout.custom_toast_maps_add_1, constraintToastMaps1)
        Toast(this).apply {
            duration = Toast.LENGTH_SHORT
            setGravity(Gravity.TOP, 0, 20)
            view = layoutToast
        }.show()
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("E HH:mm", Locale.getDefault()).format(Date())
    }
}





/**
 *
 * isLocationPermissionGranted()
 *
 * private fun isLocationPermissionGranted(): Boolean {
return if (ActivityCompat.checkSelfPermission(
this,
android.Manifest.permission.ACCESS_COARSE_LOCATION
) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
this,
android.Manifest.permission.ACCESS_FINE_LOCATION
) != PackageManager.PERMISSION_GRANTED
) {
val requestcode = 0
ActivityCompat.requestPermissions(
this,
arrayOf(
android.Manifest.permission.ACCESS_FINE_LOCATION,
android.Manifest.permission.ACCESS_COARSE_LOCATION
),
requestcode
)
false
} else {
true
}

}


val layoutGuardarLocalizacion = findViewById<ConstraintLayout>(R.id.item_google_maps)
layoutGuardarLocalizacion.setOnClickListener {
val coordenadasIntent = Intent(this, MainActivity::class.java)
//coordenadasIntent.putExtra("Latitud",  )
}

val datosLatLng = Intent(this, MainActivity::class.java)
datosLatLng.putExtra("latitud", location.latitude)
datosLatLng.putExtra("longitud", location.longitude)
 */
