package com.example.ramenmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.practice0819_2.RamenMap
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "main") {
                composable("main") { MainScreen(navController) }
                composable("stamp") { StampScreen().Content(navController) }

            }
        }
    }

    @Composable
    fun MainScreen(navController: androidx.navigation.NavHostController) {
        val map = RamenMap(15f)

        Column(modifier = Modifier.fillMaxSize()) {
            // 上部70%にGoogle Mapを表示
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
            ) {
                map.Content()
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
                    // ボタンのクリック処理をここに記述
                    navController.navigate("stamp")

                }) {
                    Text(text = "ステータス")
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainActivity().MainScreen(rememberNavController())
}