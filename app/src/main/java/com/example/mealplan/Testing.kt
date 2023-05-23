package com.example.mealplan

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class Testing : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var searchView: SearchView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var foodList: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var currentUserUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)

        val buttonClick = findViewById<Button>(R.id.PressToContinue)
        buttonClick.setOnClickListener {
            val intent = Intent(this, MealPlan2::class.java)
            startActivity(intent)
        }

        listView = findViewById(R.id.listView)
        searchView = findViewById(R.id.searchView)

        firestore = FirebaseFirestore.getInstance()
        foodList = mutableListOf()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, foodList)
        listView.adapter = adapter

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUserUid = currentUser?.uid ?: ""

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterFoodList(newText)
                return false
            }
        })

        retrieveFoodData()
    }

    private fun retrieveFoodData() {
        firestore.collection("FOODDATA!")
            .get()
            .addOnSuccessListener { querySnapshot ->
                foodList.clear()
                for (document in querySnapshot.documents) {
                    val food = document.getString("Food")
                    val calorie = document.getDouble("Calorie")
                    if (food != null && calorie != null) {
                        val documentId = document.id
                        foodList.add("$documentId - $food - $calorie")
                    }
                }
                adapter.notifyDataSetChanged()
                listView.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        showPrompt(foodList[position], position)
                    }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun filterFoodList(query: String?) {
        foodList.clear()
        if (!query.isNullOrBlank()) {
            firestore.collection("FOODDATA!")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val food = document.getString("Food")
                        val calorie = document.getDouble("Calorie")
                        if (food != null && calorie != null && food.contains(query, ignoreCase = true)) {
                            val documentId = document.id
                            foodList.add("$documentId - $food - $calorie")
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    // Handle error
                }
        } else {
            retrieveFoodData()
        }
    }

    private fun showPrompt(food: String, position: Int) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Select a Meal")
            .setMessage("Choose a meal for $food")
            .setPositiveButton("Breakfast") { _, _ ->
                copyDocumentToSubcollection(food, position, "TemporaryBreakfast")
            }
            .setNegativeButton("Lunch") { _, _ ->
                copyDocumentToSubcollection(food, position, "TemporaryLunch")
            }
            .setNeutralButton("Dinner") { _, _ ->
                copyDocumentToSubcollection(food, position, "TemporaryDinner")
            }
            .create()

        alertDialog.show()
    }

    private fun copyDocumentToSubcollection(food: String, position: Int, subcollection: String) {
        val selectedDocumentId = food.split(" - ")[0]
        val randomSuffix = generateRandomSuffix() // Generate a random number as a suffix
        val copiedDocumentId = "$selectedDocumentId-$randomSuffix" // Append the random suffix to the document ID
        val selectedDocumentRef = firestore.collection("FOODDATA!").document(selectedDocumentId)
        val currentUserSubcollectionRef =
            firestore.collection("users").document(currentUserUid).collection(subcollection)

        selectedDocumentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val documentData = documentSnapshot.data
                    if (documentData != null) {
                        currentUserSubcollectionRef.document(copiedDocumentId)
                            .set(documentData)
                            .addOnSuccessListener {
                                // Success
                            }
                            .addOnFailureListener { exception ->
                                // Handle error
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun generateRandomSuffix(): String {
        val random = Random()
        val randomSuffix = StringBuilder()
        repeat(6) {
            randomSuffix.append(random.nextInt(10))
        }
        return randomSuffix.toString()
    }
}
