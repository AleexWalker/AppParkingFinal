package com.example.appparking.viewModels

import androidx.lifecycle.ViewModel
import com.example.appparking.places.repo.AppRepo


class LocationViewModel : ViewModel() {

    private val repo = AppRepo()

    fun getNearByPlace(url: String) = repo.getPlaces(url)

}