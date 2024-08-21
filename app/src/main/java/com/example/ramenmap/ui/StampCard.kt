package com.example.ramenmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry

class StampCard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
                Column( // Surfaceを使って背景色を設定
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray)
                ) {
                    Input() // すべてのUIコンポーネントをここで呼び出す
                }
        }
    }
}

@Composable
fun Input(){
    ArticleText(
        title = "Information",
        topic1 = "demo",
        topic2 = "demo")
}

@Composable
fun ArticleText(title: String, topic1: String, topic2: String, modifier: Modifier = Modifier) {
//    val image = painterResource(id = R.drawable.bg_compose_background)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 4.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally){
//        Image(
//            painter = image,
//            contentDescription = null
//        )
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp,bottom = 16.dp)
        )
//        Text(
//            text = topic1,
//            modifier = modifier
//        )
//        Text(
//            text = topic2,
//            modifier = modifier
//        )
        Scroll()
        RadarChartView()
        Push(sms = 2657)
    }
}

@Composable
fun Push(sms:Int){
    // State to manage the message
    var message by remember { mutableStateOf("") }
    var count by remember { mutableIntStateOf(0) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        Column(
            modifier = Modifier.padding(end = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    count += 1
                    message = "$count/$sms"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Back",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            // Display the message
            if (message.isNotEmpty()) {
                Text(text = message,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 24.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}

@Composable
fun Scroll(){
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .size(400.dp, 150.dp)
            .padding(8.dp)
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        )
        {
            Column {
                Text(
                    text = "あなたのスタンプラリー履歴",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "・ラーメン屋タロウ\n ・次郎\n・三郎\n・史郎\n・五郎",
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun RadarChartView() {
    // Sample data
    val entries = listOf(
        RadarEntry(0f, 5f),  // x 値（インデックス）、y 値（データ）
        RadarEntry(1f, 3f),
        RadarEntry(2f, 4f),
        RadarEntry(3f, 2f)
    )

    val dataSet = RadarDataSet(entries, "Sample Data").apply {
        color = android.graphics.Color.BLUE
        valueTextColor = android.graphics.Color.BLACK
        valueTextSize = 12f
    }

    val radarData = RadarData(dataSet)
    Box(
        modifier = Modifier
            .size(400.dp, 400.dp)
            .padding(8.dp)
            .background(Color.White)
    ){
        // Create RadarChart
        AndroidView(
            factory = { context ->
                RadarChart(context).apply {
                    this.data = radarData
                    description.isEnabled = false
                    webColor = android.graphics.Color.GRAY
                    webColorInner = android.graphics.Color.LTGRAY
                    webLineWidth = 1f
                    webLineWidthInner = 1f
                    webAlpha = 100
                    invalidate() // Refresh chart
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun BarChartView() {
    // Sample data
    val entries = listOf(
        BarEntry(0f, 5f),
        BarEntry(1f, 3f),
        BarEntry(2f, 4f),
        BarEntry(3f, 2f)
    )

    val dataSet = BarDataSet(entries, "Sample Data").apply {
        color = Color.Gray.toArgb()
        valueTextColor = Color(0xFF5733).toArgb()
        valueTextSize = 12f
    }

    val barData = BarData(dataSet)
    Box(
        modifier = Modifier
            .size(400.dp, 400.dp)
            .padding(8.dp)
            .background(Color.White)
    ) {
        // Create BarChart
        AndroidView(
            factory = { context ->
                BarChart(context).apply {
                    this.data = barData
                    description.isEnabled = false
                    invalidate() // Refresh chart
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InformationPreview() {
    Input()
}