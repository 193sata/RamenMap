package com.example.ramenmap

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import java.io.BufferedReader
import java.io.InputStreamReader

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
        title = "Information")
}

@Composable
fun ArticleText(title: String) {
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
        CSVDisplayScreen()
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
fun loadCSVFromAssets(context: Context, fileName: String): List<String> {
    val csvLines = mutableListOf<String>()
    try {
        val assetManager = context.assets
        val inputStream = assetManager.open(fileName)
        BufferedReader(InputStreamReader(inputStream)).useLines { lines ->
            csvLines.addAll(lines)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return csvLines
}

@Composable
fun RadarChartView() {
    var salt by remember { mutableIntStateOf(1)}
    var soy by remember { mutableIntStateOf(1)}
    var pork by remember { mutableIntStateOf(1)}
    var jiro by remember { mutableIntStateOf(1)}
    val context = LocalContext.current
    var csvData by remember { mutableStateOf(listOf<String>()) }
    csvData = loadCSVFromAssets(context, "ramen_shop_data.csv").take(11)
    for (lines in csvData){
        val listLine: List<String> = lines.split(",")
        val type = listLine[3]
            when(type){
            "塩" -> salt += 1
            "しょうゆ" -> soy += 1
            "とんこつ" -> pork += 1
            "創作"  -> jiro += 1
                else -> continue
        }
    }
    val entries = listOf(
        RadarEntry(salt.toFloat(), 1),
        RadarEntry(soy.toFloat(), 1),
        RadarEntry(pork.toFloat(), 1),
        RadarEntry(jiro.toFloat(), 1)
    )

    val dataSet = RadarDataSet(entries, "あなたの傾向").apply {
        color = android.graphics.Color.BLUE
        valueTextColor = android.graphics.Color.BLACK
        valueTextSize = 30f

//        valueFormatter = object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                return when {
//                    value >= 4 -> "High"
//                    value >= 2 -> "Medium"
//                    else -> "Low"
//                }
//            }
//        }
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
                    yAxis.apply{
                        axisMinimum = 0f
                        labelCount = 5
                        textColor = android.graphics.Color.BLACK
                        setDrawLabels((false))
                    }
                    invalidate()
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun CSVDisplayScreen() {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .size(400.dp, 150.dp)
            .padding(8.dp)
            .background(Color.White)
    ) {
        val context = LocalContext.current
        var csvData by remember { mutableStateOf(listOf<String>()) }
        csvData = loadCSVFromAssets(context, "ramen_shop_data.csv").take(11)
        // 後ほど修正(LaunchedEffect?)
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)) {
            Text(text = "直近１０件の訪問履歴", fontSize = 18.sp,modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            // `for`文でCSVの各行を順に表示
            for (lines in csvData) {
                val listLine: List<String> = lines.split(",")
                val nameTime: List<String> = listOf(listLine[0], "★"+listLine[4])
                Text(text = nameTime.joinToString("  ")) // 各行のテキストを表示
                HorizontalDivider() // 行の間に区切り線を表示
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InformationPreview() {
    Input()
}