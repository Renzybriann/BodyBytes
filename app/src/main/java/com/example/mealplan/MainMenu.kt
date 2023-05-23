package com.example.mealplan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
class  MainMenu : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        auth = FirebaseAuth.getInstance()

        // Initialize logout button and set listener
        val buttonClick2 = findViewById<Button>(R.id.SETTERS)
        buttonClick2.setOnClickListener {
            val intent3 = Intent(this, Settings::class.java)
            startActivity(intent3)
        }
        val buttonClick3 = findViewById<Button>(R.id.TestButton)
        buttonClick3.setOnClickListener {
            val intent3 = Intent(this, Testing::class.java)
            startActivity(intent3)
        }



        val buttonClick = findViewById<Button>(R.id.mealButton1)
        buttonClick.setOnClickListener {
            val intent = Intent(this, MealPlan1::class.java)
            startActivity(intent)
        }


        val buttonClick1 = findViewById<Button>(R.id.WeightButton)
        buttonClick1.setOnClickListener {
            val intent2 = Intent(this, WeightCompare::class.java)
            startActivity(intent2)
        }
    }
}