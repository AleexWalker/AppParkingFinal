package com.example.appparking

import com.google.android.gms.maps.model.LatLng
import java.util.*

data class PlaceModel (
    val id: String,
    val icon: String,
    val name: String,
    val geometry: String)
{
    override fun equals(other: Any?): Boolean {
        if (other !is PlaceModel) {
            return false
        }
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }
}

//fun PlaceModel.getPositionVector(azimuth: Float, latLng: LatLng): Vector1 {

data class GeometryLocation(
    val lat: Double,
    val lng: Double)
{
    val latLng : LatLng
    get() = LatLng(lat, lng)
}