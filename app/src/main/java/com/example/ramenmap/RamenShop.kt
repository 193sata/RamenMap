package com.example.ramenmap

data class RamenShop(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val distance: Double,
    val type: String,
    val review: Int
)