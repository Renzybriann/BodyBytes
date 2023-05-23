package com.example.mealplan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class ChooseMealPlan : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_meal_plan)
        supportActionBar?.hide()

        val buttonClick = findViewById<Button>(R.id.AddMeal)
       buttonClick.setOnClickListener {
           val intent = Intent(this, UserBmi::class.java)
            startActivity(intent)
        }


        val buttonClick1 = findViewById<Button>(R.id.Meal1)
        buttonClick1.setOnClickListener {
            val intent1 = Intent(this, MealPlanList::class.java)
            startActivity(intent1)

        }

        val buttonClick2 = findViewById<Button>(R.id.Meal2)
        buttonClick2.setOnClickListener {
            val intent2 = Intent(this, MealPlanDate::class.java)
            startActivity(intent2)
        }





    }
}