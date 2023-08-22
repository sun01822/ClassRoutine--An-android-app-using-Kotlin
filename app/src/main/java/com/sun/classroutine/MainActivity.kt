package com.sun.classroutine

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sun.classroutine.adapter.TodayClassAdapter
import com.sun.classroutine.data.ClassInfo
import com.sun.classroutine.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TodayClassAdapter
    private val url =
        "https://docs.google.com/spreadsheets/d/1R7_Aqq7WGvZz5kho97Y97e1p1FNsGpa2/edit#gid=461023197"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView
        val recyclerView = binding.todayClassRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TodayClassAdapter(emptyList()) // Initialize with an empty list
        recyclerView.adapter = adapter

        // Set a click listener for the "Fetch Data" button
        binding.fetchDataButton.setOnClickListener {
            val semester = binding.semesterEditText.text.toString()
            val section = binding.sectionEditText.text.toString()
            val currentDay = getCurrentDay()

            // Fetch class data from Excel sheet
            fetchDataFromExcel(currentDay, semester, section)
        }
    }

    private fun getCurrentDay(): String {
        val calendar = Calendar.getInstance()

        // Map the day of the week to its name
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> "Unknown"
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchDataFromExcel(
        dayOfWeek: String,
        semester: String,
        section: String
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Download the Excel file from the URL and save it locally
                val excelFile = File(cacheDir, "class_schedule.xlsx")
                val urlConnection = URL(url).openConnection()
                urlConnection.connect()

                urlConnection.getInputStream().use { inputStream ->
                    FileOutputStream(excelFile).use { outputStream ->
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }
                    }
                }

                // Now, open the downloaded Excel file with Apache POI
                WorkbookFactory.create(excelFile).use { workbook ->
                    // Get the sheet that contains the data.
                    val sheet = workbook.getSheet("Sheet1")

                    // Create a list to store the data.
                    val data = mutableListOf<ClassInfo>()

                    // Iterate over the rows in the sheet.
                    for (rowNum in 0..sheet.lastRowNum) {
                        // Get the values in the current row.
                        val row = sheet.getRow(rowNum)
                        if (row != null) {
                            val values = row.map { it.toString() }

                            // If the day of the week, semester, and section match, add the data to the list.
                            if (values.getOrNull(0) == dayOfWeek && values.getOrNull(1) == semester && values.getOrNull(2) == section) {
                                val classInfo = ClassInfo(
                                    values[0], // day of the week
                                    values[1], // semester
                                    values[2], // section
                                    values[3], // subject
                                    values[4], // teacher
                                    values[5] // start time
                                )

                                data.add(classInfo)
                            }
                        }
                    }

                    // Update the RecyclerView adapter with the fetched data on the main thread.
                    withContext(Dispatchers.Main) {
                        adapter.updateData(data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error, e.g., show an error message to the user
            }
        }
    }
}
