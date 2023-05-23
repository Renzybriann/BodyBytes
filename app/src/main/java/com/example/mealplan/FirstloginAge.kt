package com.example.mealplan
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirstloginAge : AppCompatActivity() {

    // initialize Firebase authentication and Firestore database

    private lateinit var birthdayDayEditText: EditText
    private lateinit var birthdayMonthEditText: EditText
    private lateinit var birthdayYearEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var submitButton: Button

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firstlogin_age)


        birthdayDayEditText = findViewById(R.id.Days1)
        birthdayMonthEditText = findViewById(R.id.Month1)
        birthdayYearEditText = findViewById(R.id.Year1)
        ageEditText = findViewById(R.id.Age1)
        submitButton = findViewById(R.id.Submission)

        submitButton.setOnClickListener {
            val birthdayDay = birthdayDayEditText.text.toString().toIntOrNull()
            val birthdayMonth = birthdayMonthEditText.text.toString().toIntOrNull()
            val birthdayYear = birthdayYearEditText.text.toString().toIntOrNull()
            val age = ageEditText.text.toString().toIntOrNull()

            if (birthdayDay == null || birthdayMonth == null || birthdayYear == null || age == null) {
                Toast.makeText(this, "Please enter valid integer values for birthday and age", Toast.LENGTH_SHORT).show()
            } else {
                val userDocRef = db.collection("users").document(currentUser?.uid ?: "")
                val userInfoDocRef = userDocRef.collection("userinfos").document("agedata")

                val userInfo = hashMapOf(
                    "birthdayDay" to birthdayDay,
                    "birthdayMonth" to birthdayMonth,
                    "birthdayYear" to birthdayYear,
                    "age" to age
                )

                userInfoDocRef.set(userInfo)
                    .addOnSuccessListener {
                        val intent = Intent(this, MainMenu::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save user info", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}