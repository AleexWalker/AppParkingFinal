package com.example.appparking.places.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import com.example.appparking.databinding.InfoWindowLayoutBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.SphericalUtil
import kotlin.math.roundToInt

class InfoWindowAdapter(private val location: Location, context: Context) :
    GoogleMap.InfoWindowAdapter {
    private val binding: InfoWindowLayoutBinding = InfoWindowLayoutBinding.inflate(
        LayoutInflater.from(context), null, false
    )

    @SuppressLint("SetTextI18n")
    override fun getInfoWindow(marker: Marker): View {

        val distance = SphericalUtil.computeDistanceBetween(
            LatLng(
                location.latitude, location.longitude
            ), marker.position
        )

        if (distance.roundToInt() > 1000) {
            val kilometers = (distance / 1000).roundToInt()
        }

        val speed = location.speed
        if (speed.roundToInt() > 0) {
            val time = distance / speed

        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun getInfoContents(marker: Marker): View {

        val distance = SphericalUtil.computeDistanceBetween(
            LatLng(
                location.latitude, location.longitude
            ), marker.position
        )

        if (distance.roundToInt() > 1000) {
            val kilometers = (distance / 1000).roundToInt()

        }

        val speed = location.speed
        if (speed.roundToInt() > 0) {
            val time = distance / speed

        }
        return binding.root
    }
}