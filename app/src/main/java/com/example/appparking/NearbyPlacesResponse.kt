package com.example.appparking

import com.google.gson.annotations.SerializedName

data class NearbyPlacesResponse (@SerializedName("results") val results: List<PlaceModel>)