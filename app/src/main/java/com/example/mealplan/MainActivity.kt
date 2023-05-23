package com.example.mealplan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class  MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val buttonClick = findViewById<Button>(R.id.PressToContinue)
        buttonClick.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }


       // val buttonClick1 = findViewById<Button>(R.id.button7)
       // buttonClick1.setOnClickListener {
       //     val intent2 = Intent(this, WeightCompare::class.java)2
       //     startActivity(intent2)
       // }
    }
}