package com.example.mealplan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.CheckBox
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.FirebaseApp
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Month
import android.view.View

class FirstLogin : AppCompatActivity() {


    private lateinit var weightEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var birthdayDayEditText: EditText
    private lateinit var birthdayMonthEditText: EditText
    private lateinit var birthdayYearEditText: EditText
    private lateinit var ageEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_login)


        weightEditText = findViewById(R.id.InsertWeight)
        heightEditText = findViewById(R.id.InsertHeight)
        submitButton = findViewById(R.id.Submission)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        submitButton.setOnClickListener {
            onSubmitButtonClicked(it)
        }
    }

    fun calculateBMI(view: View) {
        val weight = weightEditText.text.toString().toFloatOrNull() ?: 0f
        val height = heightEditText.text.toString().toFloatOrNull() ?: 0f

        val bmi = calculateBMI(weight, height)

        // Print the BMI on the screen
        val bmiTextView = findViewById<TextView>(R.id.ViewB_m_i)
        bmiTextView.text = "Your BMI is $bmi"
    }

    fun onSubmitButtonClicked(view: View) {
        val weight = weightEditText.text.toString().toFloatOrNull() ?: 0f
        val height = heightEditText.text.toString().toFloatOrNull() ?: 0f


        val bmi = calculateBMI(weight, height)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userInfo = hashMapOf(
                "weight" to weight,
                "height" to height,
                "bmi" to bmi

            )

            firestore.collection("users").document(currentUser.uid).collection("userinfos")
                .document("bmidata")
                .set(userInfo)
                .addOnSuccessListener {
                    val intent = Intent(this, FirstloginAge::class.java)
                    startActivity(intent)
                    finish()
                }
        }
    }

    private fun calculateBMI(weight: Float, height: Float): Float {
        return if (weight > 0 && height > 0) {
            weight / (height * height)
        } else {
            0f
        }
    }
}