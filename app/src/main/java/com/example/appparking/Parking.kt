package com.example.appparking

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.appparking.carlocation.LocationAcceder

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.example.appparking.databinding.ActivityParkingBinding
import com.google.maps.PlaceAutocompleteRequest
import kotlinx.android.synthetic.main.activity_parking.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Parking : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityParkingBinding
    private lateinit var location: Location
    private lateinit var placesService: PlacesService
    private lateinit var places: PlaceAutocompleteRequest

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityParkingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        enableLocation()

        val currentLocation = LatLng(location.latitude, location.longitude)

        item_parking.setOnClickListener {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
        }
    }

    private fun parkingsCercanos() {
        val apiKeyGoogle = this.getString(R.string.API_KEY)
        placesService.nearbyPlaces(
            keyApi = apiKeyGoogle,
            location = "${location.latitude},${location.longitude}",
            radiusInMeters = 4000,
            placeType = "parking"
        ).enqueue(
            object : Callback<NearbyPlacesResponse> {
                override fun onResponse(
                    call: Call<NearbyPlacesResponse>,
                    response: Response<NearbyPlacesResponse>
                ) {
                    if (!response.isSuccessful) {
                        Log.e(TAG, "No se ha conseguido cargar los parkins cercanos")
                        return
                    }
                    val places = response.body()?.results ?: emptyList()
                    //this@Parking.places = places as MutableList<PlaceModel>
                }

                override fun onFailure(call: Call<NearbyPlacesResponse>, t: Throwable) {
                    Log.e(TAG, "No se ha conseguido cargar los parkins cercanos", t)
                }
            }
        )
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this, android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation() {
        if (!::mMap.isInitialized) return
        if (isLocationPermissionGranted()) {
            mMap.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )) {
            Toast.makeText(this, "Acepta los permisos de GPS desde ajustes", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                Parking.REQUEST_CODE_LOCATION
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Parking.REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true
            } else {
                Toast.makeText(this, "Acepta los permisos de GPS desde ajustes", Toast.LENGTH_SHORT).show()
            } else -> {}
        }
    }
}