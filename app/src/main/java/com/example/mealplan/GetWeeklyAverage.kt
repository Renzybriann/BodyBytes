package com.example.mealplan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.graphics.Color
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import android.util.Log
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*






class GetWeeklyAverage : AppCompatActivity() {


    private val TAG = "Chart"
    private lateinit var lineChart: LineChart

    // Get an instance of the Firestore database
    private val db = FirebaseFirestore.getInstance()

    // Get the current user from FirebaseAuth
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_weekly_average)

// Find the line chart UI element by its ID
        lineChart = findViewById(R.id.chart)

        // Create an empty array to hold the data
        val usersData = mutableListOf<Map<String, Any>>()

        // Retrieve the data from Firestore in descending order of timestamp
        db.collection("users")
            .document(user?.uid ?: "")
            .collection("weights")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Add each document's data to the usersData array
                    val userData = document.data
                    usersData.add(userData)
                }

                // Group the data in sets of 7 and find the average of each set
                val averages = mutableListOf<Float>()
                var group = mutableListOf<Map<String, Any>>()
                for (i in usersData.indices) {
                    group.add(usersData[i])
                    if (group.size == 7 || i == usersData.size - 1) {
                        val sum =
                            group.sumByDouble { it["weight"].toString().toDoubleOrNull() ?: 0.0 }
                                .toFloat()
                        val average = sum / group.size
                        averages.add(average)
                        group.clear()
                    }
                }

                // Reverse the order of the averages so that they are in ascending order of timestamp
                averages.reverse()

                // Create a line data set for the chart
                val entries = mutableListOf<Entry>()
                for (i in averages.indices) {
                    val x = i.toFloat()
                    val y = averages[i]
                    Log.d(TAG, "Adding entry x=$x, y=$y")
                    entries.add(Entry(x, y))
                }

                if (entries.isNotEmpty()) {
                    // Set up the line data set
                    val dataSet = LineDataSet(entries, "Data Set")
                    dataSet.color = ColorTemplate.rgb("#F44336")
                    dataSet.valueTextColor = ColorTemplate.rgb("#000000")

                    // Create a line data object
                    val lineData = LineData(dataSet)

                    // Set the data for the line chart
                    lineChart.data = lineData

                    // Refresh the chart
                    lineChart.invalidate()
                } else {
                    Log.d(TAG, "No data found for user ${user?.uid}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
}