package com.example.mealplan

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChartFragment : Fragment() {
    private val TAG = "Chart"
    private lateinit var dailyLineChart: LineChart
    private lateinit var weeklyLineChart: LineChart

    // Get an instance of the Firestore database
    private val db = FirebaseFirestore.getInstance()

    // Get the current user from FirebaseAuth
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val vieww = inflater.inflate(R.layout.fragment_chart, container, false)

        dailyLineChart = vieww.findViewById(R.id.daily_progress_chart)

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
                    dailyLineChart.data = lineData

                    // Refresh the chart
                    dailyLineChart.invalidate()
                } else {
                    Log.d(TAG, "No data found for user ${user?.uid}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }




        //weekly chart

        weeklyLineChart = vieww.findViewById(R.id.weekly_progress_chart)

        // Create an empty array to hold the data

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
                    weeklyLineChart.data = lineData

                    // Refresh the chart
                    weeklyLineChart.invalidate()
                } else {
                    Log.d(TAG, "No data found for user ${user?.uid}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        return vieww
    }


}