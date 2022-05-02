package com.example.appparking.models.GooglePlaceModel

import com.squareup.moshi.Json

data class PhotoModel(
    @field:Json(name = "height")

    val height: Int?,

    @field:Json(name = "html_attributions")

    val htmlAttributions: List<String>?,

    @field:Json(name = "photo_reference")

    val photoReference: String?,

    @field:Json(name = "width")

    val width: Int?
) {

}