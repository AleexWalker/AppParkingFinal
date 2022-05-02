package com.example.appparking

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesService {
    @GET("nearbysearch/json")
    fun nearbyPlaces(
        @Query("key") keyApi: String,
        @Query("location") location: String,
        @Query("radius") radiusInMeters: Int,
        @Query("type") placeType: String,
    ): Call<NearbyPlacesResponse>
}