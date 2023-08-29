package com.sun.classroutine

import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import com.sun.classroutine.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val sheetID = "1IkWnYEBIKQK9Jlkuooo11oEM5mtz9DUc"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

        val data = fetchScheduleDataFromApi("Tue")
        parseScheduleJson(data)
    }

    private fun fetchScheduleDataFromApi(day: String): String {
        val urlStr = "https://docs.google.com/spreadsheets/d/$sheetID/gviz/tq?tqx=out:json&sheet=$day&headers=0"
        val url = URL(urlStr)
        val connection = url.openConnection() as HttpURLConnection
        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val response = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            response.append(line)
        }
        reader.close()
        return response.toString()
    }

    private fun parseScheduleJson(jsonData: String) {
        println("JSON DATA: $jsonData\n\n\n\n")
    }
}
