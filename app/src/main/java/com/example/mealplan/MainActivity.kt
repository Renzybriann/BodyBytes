package com.example.mealplan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button

class  MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        Handler().postDelayed({
            startActivity(Intent(this@MainActivity, Login::class.java))
            finish()
        }, 4000)


       // val buttonClick1 = findViewById<Button>(R.id.button7)
       // buttonClick1.setOnClickListener {
       //     val intent2 = Intent(this, WeightCompare::class.java)2
       //     startActivity(intent2)
       // }
    }
}