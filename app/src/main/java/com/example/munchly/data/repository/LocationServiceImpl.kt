package com.example.munchly.data.repository

import android.content.Context
import android.location.Geocoder
import com.example.munchly.domain.repository.LocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationHelper(private val context: Context) {
    suspend fun getCoordinates(address: String): Pair<Double, Double>? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context)
                val results = geocoder.getFromLocationName(address, 1)
                if (!results.isNullOrEmpty()) {
                    Pair(results[0].latitude, results[0].longitude)
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }
}