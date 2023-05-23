package com.example.mealplan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.lang.Thread.sleep

class  MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val thread = Thread {
            try {
                sleep(2000)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                val newIntent = Intent(this@MainActivity, LoginOrSignup::class.java)
                startActivity(newIntent)
                finish()
            }
        }
        thread.start()
//r
       // val buttonClick1 = findViewById<Button>(R.id.button7)
       // buttonClick1.setOnClickListener {
       //     val intent2 = Intent(this, WeightCompare::class.java)2
       //     startActivity(intent2)
       // }
    }
}