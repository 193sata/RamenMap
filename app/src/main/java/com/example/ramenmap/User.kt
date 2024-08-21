package com.example.ramenmap

import android.content.Context
import android.location.Location
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class User(context: Context) {
    var currentLocation = mutableStateOf<Location?>(null)
    var recentLogs = mutableStateOf<List<String>>(listOf())
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val logFile = File(context.getExternalFilesDir(null), "visit_log.csv")

    init {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation.value = location
                }
            }
        } catch (e: SecurityException) {
            // Handle permission denial
        }
    }

    fun logVisit(ramenShopName: String, ramenType: String) {
        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPAN).format(Date())
        try {
            PrintWriter(FileWriter(logFile, true)).use { out ->
                out.println("" +
                        "$ramenShopName, $ramenType, $timeStamp, ${currentLocation.value?.latitude}, ${currentLocation.value?.longitude}")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        loadRecentLogs()
    }

    private fun loadRecentLogs() {
        try {
            val lines = BufferedReader(FileReader(logFile)).useLines { it.toList() }
            recentLogs.value = lines.takeLast(5).reversed()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}