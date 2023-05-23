package com.example.mealplan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.EditText

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
class WeightCompare : AppCompatActivity() {

    private lateinit var weightEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var resultTextView: TextView

    // Get an instance of the Firestore database
    private val db = FirebaseFirestore.getInstance()

    // Get the current user from FirebaseAuth
    private val user = FirebaseAuth.getInstance().currentUser




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_compare)

        val buttonClick = findViewById<Button>(R.id.progress)
        buttonClick.setOnClickListener {
            val intent = Intent(this, GetWeeklyAverage::class.java)
            startActivity(intent)
        }
        val buttonClick1 = findViewById<Button>(R.id.dayprog)
        buttonClick1.setOnClickListener {
            val intent67 = Intent(this, Chart::class.java)
            startActivity(intent67)
        }
        // Find the UI elements by their IDs
        weightEditText = findViewById(R.id.input_text)
        submitButton = findViewById(R.id.compare_button)
        resultTextView = findViewById(R.id.result_text)

        submitButton.setOnClickListener {
            // Get the weight input from the user
            val weight = weightEditText.text.toString().toDoubleOrNull()

            if (weight != null) {
                // Retrieve the user's weight data from Firestore
                db.collection("users").document(user?.uid ?: "").collection("weights")
                    .orderBy("timestamp")
                    .get()
                    .addOnSuccessListener { documents ->
                        // Convert the retrieved documents to a list of weights
                        val weightsList = documents.mapNotNull { it.getDouble("weight") }

                        if (weightsList.isNotEmpty()) {
                            // If there are existing weight records, compare the latest with the inputted weight
                            val latestWeight = weightsList.last()
                            val difference = latestWeight - weight
                            resultTextView.text = "Latest weight: $latestWeight\n" +
                                    "Inputted weight: $weight\n" +
                                    "Difference: $difference"
                        } else {
                            // If there are no existing weight records, show a message to the user
                            resultTextView.text = "This is your first weight record data. " +
                                    "There is nothing to compare."
                        }

                        // Add the inputted weight to the user's weight records in Firestore
                        db.collection("users").document(user?.uid ?: "").collection("weights")
                            .add(hashMapOf("weight" to weight, "timestamp" to System.currentTimeMillis()))
                    }
            }
        }
    }
}