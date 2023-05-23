package com.example.mealplan

import android.os.Bundle
import android.os.Handler
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MealPlan3 : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var textView: TextView
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_plan3)

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        textView = findViewById(R.id.textView)
        listView = findViewById(R.id.listView)
    }

    override fun onStart() {
        super.onStart()

        // Delay the retrieval and population of data for 5 seconds
        Handler().postDelayed({
            // Retrieve data from Firestore and populate the ListView
            retrieveDataAndPopulateListView()
        }, 5000) // Delay in milliseconds (5 seconds)
    }

    private fun retrieveDataAndPopulateListView() {
        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        val userRef = firestore.collection("users").document(uid!!)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val selectID = document.getString("Select")
                    val mealPlanRef = userRef.collection("Mealplan").document(selectID!!)
                    mealPlanRef.get()
                        .addOnSuccessListener { mealPlanDoc ->
                            if (mealPlanDoc != null) {
                                val nameID = mealPlanDoc.getString("name")
                                textView.text = "SelectID: $selectID\nNameID: $nameID"

                                // Create a list to store the retrieved data
                                val dataList = mutableListOf<String>()

                                // Check for subcollections with the same name as NameID
                                firestore.collectionGroup(nameID!!)
                                    .get()
                                    .addOnSuccessListener { subcollectionQuerySnapshot ->
                                        // Iterate through the subcollection documents
                                        for (subDoc in subcollectionQuerySnapshot) {
                                            // Retrieve the fields from each subcollection document
                                            val food = subDoc.getString("Food")
                                            val calorie = subDoc.getLong("Calorie")
                                            val time = subDoc.getString("Time")

                                            // Append the retrieved data to the list
                                            if (food != null && calorie != null && time != null) {
                                                dataList.add("Food: $food\nCalorie: $calorie\nTime: $time")
                                            }
                                        }

                                        // Create an ArrayAdapter to display the list items in the ListView
                                        val adapter = ArrayAdapter(this@MealPlan3, android.R.layout.simple_list_item_1, dataList)

                                        // Set the adapter to the ListView
                                        listView.adapter = adapter
                                    }
                                    .addOnFailureListener { exception ->
                                        textView.text = "Error retrieving subcollection documents: $exception"
                                    }
                            } else {
                                textView.text = "No meal plan document found for SelectID: $selectID"
                            }
                        }
                        .addOnFailureListener { exception ->
                            textView.text = "Error retrieving meal plan document: $exception"
                        }
                } else {
                    textView.text = "No document found for current user"
                }
            }
            .addOnFailureListener { exception ->
                textView.text = "Error retrieving user document: $exception"
            }
    }
}
