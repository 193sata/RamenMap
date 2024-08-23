package com.example.ramenmap

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.practice0819_2.RamenMap
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.content.pm.PackageManager
import com.example.ramenmap.ui.theme.RamenMapTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue



class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                // パーミッションが許可された場合の処理
                println("パーミッションが許可されました")
            }
            else -> {
                // パーミッションが拒否された場合の処理
                println("パーミッションが拒否されました")

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 共通の初期化処理
        checkPermissions()
        enableEdgeToEdge()

        // UIの設定
        setContent {
            RamenMapTheme {
                val navController = rememberNavController()
                val user = User(LocalContext.current)
                // for debug
                //val location = user.currentLocation.value
                //if (location != null) {
                //    println(location.latitude)
                //    println(location.longitude)
                //å}

                NavHost(navController = navController, startDestination = "main") {
                    composable("main") { MainScreen(navController, user) }
                    composable("stamp") { StampScreen().Content(navController) }
                }

                // ここでGreeting画面を直接呼び出すこともできます
                // Greeting(user)
            }
        }
    }


    @Composable
    fun MainScreen(navController: androidx.navigation.NavHostController, user: User) {
        var distance by mutableStateOf(500.0) // ここでdistanceを状態として管理
        val map = RamenMap(15f, user.retrieveLocation())

        Column(modifier = Modifier.fillMaxSize()) {
            // 上部70%にGoogle Mapを表示
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
            ) {
                map.Content() // 地図の表示
            }

            // 下部30%に現在地の座標とボタンを配置
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "現在地の座標: ${map.location.latitude}, ${map.location.longitude}")

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    navController.navigate("stamp")
                }) {
                    Text(text = "ステータス")
                }

                Button(onClick = {
                    distance += 500.0 // ここでdistanceを変更
                    print(distance)
                    map.ChangeDistance(distance)
                }) {
                    Text(text = "change distance")
                }
            }
        }
    }


    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED -> {
                requestPermissionLauncher.launch(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                )
            }
        }
    }
}

