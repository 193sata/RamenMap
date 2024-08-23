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
import com.google.android.gms.maps.model.BitmapDescriptorFactory

import com.example.ramenmap.RamenShopNavi
import com.example.ramenmap.RamenShop

class RamenMap(private val zoomLevel: Float) {
    //val user = User()
    // 現在地を保持するための変数
    //座標関係は本来ならuserインスタンスが保持
    var location by mutableStateOf(LatLng(32.81449335495487, 130.72729505562057)) // デフォルトの位置（熊本）

    // 避難所のリスト
    private var ramenShops: MutableList<RamenShop> = mutableListOf()

    private var ramenShopNaviInstance = RamenShopNavi()

    // 初期化時にCSVファイルを読み込んで避難所リストに追加
    // 現在地から半径2キロ以内を取得
    @Composable
    fun Content(modifier: Modifier = Modifier) {
        // UpdateLocationは、ユーザーがコンテンツを表示する際に呼び出されるようにする
        //user.UpdateLocation()
        val context = LocalContext.current
        ramenShopNaviInstance.setUserPosition(location.latitude, location.longitude)
        ramenShopNaviInstance.readCsvFile(context)

        try {
            val inputStream = context.resources.openRawResource(R.raw.ramen_shop_data)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val header = reader.readLine() // ヘッダーを取得
            val headerTokens = header.split(",")

            // 必要なカラムのインデックスを取得
            val nameIndex = headerTokens.indexOf("店舗名")
            val latitudeIndex = headerTokens.indexOf("緯度")
            val longitudeIndex = headerTokens.indexOf("経度")
            val typeIndex = headerTokens.indexOf("タイプ")
            val reviewIndex = headerTokens.indexOf("レビュー")

            ramenShops = ramenShopNaviInstance.getRamenShops(500.0)

//            var line: String?
//            while (reader.readLine().also { line = it } != null) {
//                val tokens = line!!.split(",")
//
//                val name = tokens.getOrNull(nameIndex)
//                val latitude = tokens.getOrNull(latitudeIndex)?.toDoubleOrNull()
//                val longitude = tokens.getOrNull(longitudeIndex)?.toDoubleOrNull()
//                val type = tokens.getOrNull(typeIndex)
//                val review = tokens.getOrNull(reviewIndex)?.toIntOrNull()
//
//                if (name != null && latitude != null && longitude != null && type != null && review != null) {
//                    // 距離を計算
//                    val distance = calculateDistance(
//                        location.latitude, location.longitude,
//                        latitude, longitude
//                    )
//                    // 500メートル以内のラーメン店のみ追加
//                    if (distance <= 500) {
//                        ramenShops.add(RamenShop(name, latitude, longitude, distance, type, review))
//                        // デバッグ用のログ出力
//                        println("Added ramen shop: $name at ($latitude, $longitude), Type: $type, Review: $review, Distance: $distance meters")
//                    }
//                } else {
//                    println("Invalid ramen shop data: $line")
//                }
//            }
//            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // 一番近い避難所を特定
        val closestShelter = ramenShops.minByOrNull { it.distance }

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
            ramenShops.forEach { ramenshop ->
                Marker(
                    position = LatLng(ramenshop.latitude, ramenshop.longitude),
                    title = ramenshop.name,
                    snippet = "${ramenshop.type} - レビュー: ${ramenshop.review}点",
                    icon = when (ramenshop) {
                        closestShelter -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                        else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    }
                )
            }
        }
    }
}