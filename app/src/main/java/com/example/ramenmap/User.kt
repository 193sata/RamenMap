package com.example.ramenmap

import android.content.Context
import android.location.Location
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
//import com.google.android.gms.tasks.OnSuccessListener

class User(context: Context) {
    var currentLocation = mutableStateOf<Location?>(null)
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    init {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation.value = location
                }
            }
        } catch (e: SecurityException) {
            // パーミッションが拒否された場合の処理
        }
    }
}