package com.example.mealplan

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import java.util.*
import android.view.View
class INSERTAGE : AppCompatActivity() {
    private lateinit var mealType: String
    private lateinit var foodItem: EditText
    private lateinit var foodList: ListView
    private lateinit var foodItems: ArrayList<String>
    private lateinit var foodListAdapter: ArrayAdapter<String>
    private lateinit var sharedPreferences: SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insertage)

        mealType = intent.getStringExtra("mealType") ?: ""
        title = mealType

        foodItem = findViewById(R.id.foodItem)
        foodList = findViewById(R.id.foodList)

        foodItems = ArrayList()
        foodListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, foodItems)
        foodList.adapter = foodListAdapter

        // Retrieve the saved food items for this meal type
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val savedFoodItems = sharedPreferences.getString(mealType, "")
        if (savedFoodItems != "") {
            val items = savedFoodItems!!.split(",")
            for (item in items) {
                foodItems.add(item)
            }
            foodListAdapter.notifyDataSetChanged()
        }

        // Save the entered food item to the list when the "Add" button is clicked
        findViewById<View>(R.id.addBtn).setOnClickListener {
            val item = foodItem.text.toString().trim()
            if (item != "") {
                foodItems.add(item)
                foodListAdapter.notifyDataSetChanged()
                foodItem.setText("")

                // Save the updated food items for this meal type
                val items = foodItems.joinToString(",")
                sharedPreferences.edit().putString(mealType, items).apply()
            }
        }

        // Remove the selected food item from the list when it is clicked
        foodList.setOnItemClickListener { parent, view, position, id ->
            foodItems.removeAt(position)
            foodListAdapter.notifyDataSetChanged()

            // Save the updated food items for this meal type
            val items = foodItems.joinToString(",")
            sharedPreferences.edit().putString(mealType, items).apply()
        }
    }
}