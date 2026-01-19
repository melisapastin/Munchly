package com.example.munchly.domain.repository

interface LocationService {
        fun getCoordinatesFromAddress(address: String): Pair<Double, Double>?

}