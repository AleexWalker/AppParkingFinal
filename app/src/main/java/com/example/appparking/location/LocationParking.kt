package com.example.appparking.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.widget.Toast

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope

import com.example.appparking.R
import com.example.appparking.places.adapter.InfoWindowAdapter
import com.example.appparking.databinding.ActivityLocationParkingBinding
import com.example.appparking.places.models.GooglePlaceModel.GooglePlaceModel
import com.example.appparking.viewModels.LocationViewModel
import com.example.appparking.places.models.GooglePlaceModel.GoogleResponseModel
import com.example.nearmekotlindemo.utility.State

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar

import java.util.ArrayList

class LocationParking : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityLocationParkingBinding
    private lateinit var googlePlaceList: ArrayList<GooglePlaceModel>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var radius = 2500
    private var currentLocation: Location
    private val locationViewModel: LocationViewModel by viewModels<LocationViewModel>()
    private var infoWindowAdapter: InfoWindowAdapter? = null

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    init {
        currentLocation = Location("parking")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationParkingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        googlePlaceList = ArrayList()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val nearbyLocation = "parking"

        enableLocation()
        setupMap()
        getNearbyPlace(nearbyLocation)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        transition()
    }

    private fun transition() {
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out)
    }

    private fun getCurrentLocation() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //isLocationPermissionOk = false
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {

            currentLocation = it
            infoWindowAdapter = null
            infoWindowAdapter = InfoWindowAdapter(currentLocation, this)
            mMap?.setInfoWindowAdapter(infoWindowAdapter)
            //moveCameraToLocation(currentLocation)
        }.addOnFailureListener {
            Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveCameraToLocation(location: Location) {

        val currentLatLong = LatLng(location.latitude, location.longitude)

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
            LatLng(
                location.latitude,
                location.longitude
            ), 17f
        )

        val markerOption = MarkerOptions()
            .position(LatLng(location.latitude, location.longitude))
            .title("Current Location")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 16f))

    }

    private fun setupMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LocationGuardar.LOCATION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            if (location != null) {
                currentLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 16f))
            }
        }
    }

    private fun getNearbyPlace(placeType: String) {
        val url = ("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                + currentLocation.latitude + "," + currentLocation.longitude
                + "&radius=" + radius + "&type=" + placeType + "&key=" +
                resources.getString(R.string.API_KEY))
        lifecycleScope.launchWhenStarted {
            locationViewModel.getNearByPlace(url).collect {
                when (it) {
                    is State.Loading -> {
                        if (it.flag == true) {
                            Toast.makeText(this@LocationParking, "", Toast.LENGTH_SHORT).show()
                        }
                    }

                    is State.Success -> {
                        Toast.makeText(this@LocationParking, "", Toast.LENGTH_SHORT).show()
                        val googleResponseModel : GoogleResponseModel = it.data as GoogleResponseModel

                        if (googleResponseModel.googlePlaceModelList != null &&
                                googleResponseModel.googlePlaceModelList.isNotEmpty()
                        ) {
                            googlePlaceList.clear()
                            mMap.clear()

                            for (i in googleResponseModel.googlePlaceModelList.indices) {
                                googlePlaceList.add(googleResponseModel.googlePlaceModelList[i])
                                addMarker(googleResponseModel.googlePlaceModelList[i], i)

                            }
                        } else {
                            mMap.clear()
                            googlePlaceList.clear()
                        }
                    }

                    is State.Failed -> {
                        Toast.makeText(this@LocationParking, "", Toast.LENGTH_SHORT).show()
                        Snackbar.make(binding.root, it.error,
                        Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun addMarker(googlePlaceModel: GooglePlaceModel, i: Int) {
        val markerOptions = MarkerOptions()
            .position(
                LatLng(
                    googlePlaceModel.geometry?.location?.lat!!,
                    googlePlaceModel.geometry.location.lng!!
                )
            )
            .title(googlePlaceModel.name)
            .snippet(googlePlaceModel.vicinity)
        markerOptions.icon(getCustomIcon())
        mMap.addMarker(markerOptions)?.tag = i
    }

    private fun getCustomIcon(): BitmapDescriptor {
        val background = ContextCompat.getDrawable(this, R.drawable.menu_cardheader_mapa)
        background?.setTint(resources.getColor(R.color.primary))
        background?.setBounds(0,0, background.intrinsicWidth, background.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(
            background?.intrinsicWidth!!, background.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        background.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this, android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
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
                LocationParking.REQUEST_CODE_LOCATION
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
            LocationParking.REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true
            } else {
                Toast.makeText(this, "Acepta los permisos de GPS desde ajustes", Toast.LENGTH_SHORT).show()
            } else -> {}
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return false
    }
}