package com.example.mealplan

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Random

private lateinit var listView: ListView
private lateinit var searchView: SearchView
private lateinit var firestore: FirebaseFirestore
private lateinit var foodList: MutableList<String>
private lateinit var adapter: ArrayAdapter<String>
private lateinit var currentUserUid: String


class MealListFragment : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vieww = inflater.inflate(R.layout.fragment_meal_list, container, false)

        val add_mealplan_btn = vieww.findViewById<Button>(R.id.add_meal_plan_btn)
        add_mealplan_btn.setOnClickListener {
            loadFragment(MealPlanDateFragment())
        }

        val back_btn = vieww.findViewById<Button>(R.id.back_btn)
        back_btn.setOnClickListener {
            loadFragment(HomeFragment())
        }


        listView = vieww.findViewById(R.id.listView)
        searchView = vieww.findViewById(R.id.searchView)

        firestore = FirebaseFirestore.getInstance()
        foodList = mutableListOf()
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, foodList)
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


        return vieww
    }

    private fun showPrompt(food: String, position: Int) {
        val alertDialog = AlertDialog.Builder(requireContext())
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

    private  fun loadFragment(fragment: Fragment){
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container,fragment)
        transaction.commit()
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



