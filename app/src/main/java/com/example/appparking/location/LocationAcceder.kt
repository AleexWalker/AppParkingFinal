package com.example.appparking.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.appparking.ui.UserMenu
import com.example.appparking.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.appparking.databinding.ActivityLocationAccederBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class LocationAcceder : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMyLocationButtonClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityLocationAccederBinding

    private lateinit var baseDatos: FirebaseFirestore

    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private var marca = String()

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationAccederBinding.inflate(layoutInflater)
        setContentView(binding.root)
        baseDatos = FirebaseFirestore.getInstance()
        baseDatos.firestoreSettings = FirebaseFirestoreSettings.Builder().build()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        with(binding){
            itemBotonAccederMapsType.imagenMapsType.setOnClickListener {
                if (mMap.mapType == GoogleMap.MAP_TYPE_NORMAL)
                    mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                else
                    mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
            itemBotonAccederMapsBack.imagenMapsBack.setOnClickListener {
                startActivity(Intent(applicationContext, UserMenu::class.java))
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        loadMapCharacteristics()
        loadMapStyle(googleMap)
        loadLocation()
        setUpMap()
    }

    private fun loadMapCharacteristics() {
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.isTrafficEnabled = true

        mMap.setOnMarkerDragListener(this)
        mMap.setOnMyLocationButtonClickListener(this)
    }

    override fun onMyLocationButtonClick(): Boolean {
        mMap.animateCamera(CameraUpdateFactory
            .newLatLngZoom(LatLng(latitud, longitud), 18f))
        return true
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        p0.showInfoWindow()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        transition()
    }

    private fun transition() {
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out)
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LocationAcceder.LOCATION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)

                mMap
                    .animateCamera(CameraUpdateFactory
                        .newLatLngZoom(currentLatLong, 18f))
            }
        }
    }

    private fun placeMarkerOnMap(title: String, latitud: Double, longitud: Double) {
        val marker: Marker? = mMap.addMarker(MarkerOptions()
            .title(title)
            .flat(false)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            .position(LatLng(latitud, longitud))
            .draggable(true)
            .visible(true))
        marker!!.showInfoWindow()
    }

    private fun loadMapStyle(googleMap: GoogleMap) {
        try {
            val succes: Boolean = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style_retro
                )
            )
            if (!succes) {
                Log.e("MapsActivity", "Style pairsing failed")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsActivity", "Can't find map style. Error: ", e)
        }
    }

    private fun loadLocation() {
        baseDatos
            .collection("Usuarios")
            .document("Data")
            .get()
            .addOnSuccessListener { result ->
                latitud = result.getString("Latitud")!!.toDouble()
                longitud = result.getString("Longitud")!!.toDouble()
                marca = result.getString("Marca").toString()
                placeMarkerOnMap(marca, latitud, longitud)
            }

    }

    override fun onMarkerDrag(p0: Marker) {
        return
    }

    override fun onMarkerDragEnd(p0: Marker) {
        return
    }

    override fun onMarkerDragStart(p0: Marker) {
        return
    }
}