package com.example.mealplan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import android.util.Log
import com.github.mikephil.charting.utils.ColorTemplate
class Chart : AppCompatActivity() {

    private val TAG = "Chart"
    private lateinit var lineChart: LineChart

    // Get an instance of the Firestore database
    private val db = FirebaseFirestore.getInstance()

    // Get the current user from FirebaseAuth
    private val user = FirebaseAuth.getInstance().currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)


        // Find the line chart UI element by its ID
        lineChart = findViewById(R.id.chart)

        // Create an empty array to hold the data
        val usersData = mutableListOf<Map<String, Any>>()

        // Retrieve the data from Firestore in ascending order of timestamp
        db.collection("users")
            .document(user?.uid ?: "")
            .collection("weights")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Add each document's data to the usersData array
                    val userData = document.data
                    usersData.add(userData)
                }

                // Create a line data set for the chart
                val entries = mutableListOf<Entry>()
                for (i in usersData.indices) {
                    val x = i.toFloat()
                    val y = usersData[i]["weight"].toString().toFloatOrNull() ?: 0f
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