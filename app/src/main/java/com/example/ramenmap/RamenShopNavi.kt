package com.example.ramenmap

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader


class RamenShopNavi {
    private var shoplist: MutableList<RamenShop> = mutableListOf()

    fun readCsvFile(context: Context) {
        try {
            // assetsフォルダ内のdata.csvファイルを開く
            val inputStream = context.assets.open("data.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                // CSVファイルの各行をログに出力
                Log.d("CSVReader", line.toString())

                // 各列のデータを取得したい場合
                val columns = line?.split(",")
                val firstColumn = columns?.get(0)
                val secondColumn = columns?.get(1)
                val thirdColum = columns?.get(2)

                // 例として、各列をログに出力
                Log.d("CSVReader", "Column 1: $firstColumn, Column 2: $secondColumn")
                shoplist.add(RamenShop(firstColumn.toString(), secondColumn.toString(), thirdColum.toString()))
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

public class RamenShop(val name: String, val latitude: String, val longitude: String) {
    init{
        println("created RamenShop instance")
    }
}
