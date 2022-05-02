package com.example.nearmekotlindemo.models.googlePlaceModel

import com.example.appparking.models.GooglePlaceModel.GooglePlaceModel
import com.squareup.moshi.Json


data class GoogleResponseModel(
    @field:Json(name = "results")
    val googlePlaceModelList: List<GooglePlaceModel>?,
    @field:Json(name = "error_message")
    val error: String?
)