package com.example.practice0819_2

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ramenmap.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.IOException
import kotlin.math.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class RamenMap(private val zoomLevel: Float) {
    //val user = User()
    // 現在地を保持するための変数
    //座標関係は本来ならuserインスタンスが保持
    var location by mutableStateOf(LatLng(32.81449335495487, 130.72729505562057)) // デフォルトの位置（熊本）

    // 避難所のリスト
    private val shelterPoints: MutableList<ShelterPoint> = mutableListOf()

    // 避難所のデータクラス
    data class ShelterPoint(val name: String, val latitude: Double, val longitude: Double, val distance: Double)

    // 地球の半径 (メートル)
    private val earthRadius = 6371000.0

    // 距離計算用関数 (ハーバサインの公式を使用)
    //ここは一番評価の高いものを表示
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    // 初期化時にCSVファイルを読み込んで避難所リストに追加
    // 現在地から半径2キロ以内を取得
    @Composable
    fun Content(modifier: Modifier = Modifier) {
        // UpdateLocationは、ユーザーがコンテンツを表示する際に呼び出されるようにする
        //user.UpdateLocation()
        val context = LocalContext.current
        try {
            val inputStream = context.resources.openRawResource(R.raw.shelters_kuma)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val header = reader.readLine() // ヘッダーを取得
            val headerTokens = header.split(",")

            // 必要なカラムのインデックスを取得
            val nameIndex = headerTokens.indexOf("施設・場所名")
            val latitudeIndex = headerTokens.indexOf("緯度")
            val longitudeIndex = headerTokens.indexOf("経度")

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val tokens = line!!.split(",")

                val name = tokens.getOrNull(nameIndex)
                val latitude = tokens.getOrNull(latitudeIndex)?.toDoubleOrNull()
                val longitude = tokens.getOrNull(longitudeIndex)?.toDoubleOrNull()

                if (name != null && latitude != null && longitude != null) {
                    // 距離を計算
                    val distance = calculateDistance(
                        location.latitude, location.longitude,
                        latitude, longitude
                    )
                    // 2キロメートル以内の避難所のみ追加
                    if (distance <= 2000) {
                        shelterPoints.add(ShelterPoint(name, latitude, longitude, distance))
                        // デバッグ用のログ出力
                        println("Added shelter: $name at ($latitude, $longitude), Distance: $distance meters")
                    }
                } else {
                    println("Invalid shelter data: $line")
                }
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // 一番近い避難所を特定
        val closestShelter = shelterPoints.minByOrNull { it.distance }

        // カメラポジションの初期設定
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(location, zoomLevel)
        }

        GoogleMap(
            modifier = modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // 現在地のピンを立てる（赤色）
            Marker(
                position = location,
                title = "現在地",
                snippet = "あなたの現在地",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )

            // 避難所のピンを立てる
            shelterPoints.forEach { shelter ->
                Marker(
                    position = LatLng(shelter.latitude, shelter.longitude),
                    title = shelter.name,
                    snippet = "避難所",
                    icon = when (shelter) {
                        closestShelter -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                        else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    }
                )
            }
        }
    }
}