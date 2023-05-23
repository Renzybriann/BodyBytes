package com.example.mealplan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LoginOrSignup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_or_signup)
        supportActionBar?.hide()

        val login_prompt = findViewById<Button>(R.id.login_prompt)
        val signup_prompt = findViewById<Button>(R.id.signup_prompt)

        login_prompt.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        signup_prompt.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }
}