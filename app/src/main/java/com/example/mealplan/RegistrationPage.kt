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
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import android.text.TextUtils
import android.widget.Toast
// Declare the UI elements
private lateinit var firstNameEditText: EditText
private lateinit var lastNameEditText: EditText
private lateinit var emailEditText: EditText
private lateinit var passwordEditText: EditText
private lateinit var confirmPasswordEditText: EditText
private lateinit var registerButton: Button

// Declare Firebase Authentication and Firestore instances
private lateinit var auth: FirebaseAuth
private lateinit var db: FirebaseFirestore


class RegistrationPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_page)

        // Initialize the UI elements
        firstNameEditText = findViewById(R.id.FirstName)
        lastNameEditText = findViewById(R.id.LastName)
        emailEditText = findViewById(R.id.Email1)
        passwordEditText = findViewById(R.id.Password1)
        confirmPasswordEditText = findViewById(R.id.ConfirmPassword)
        registerButton = findViewById(R.id.confirm1)

        // Initialize Firebase Authentication and Firestore instances
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Set up the register button click listener
        registerButton.setOnClickListener {
            // Get the values from the UI elements
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            // Check if all fields are filled in
            if (TextUtils.isEmpty(firstName) ||
                TextUtils.isEmpty(lastName) ||
                TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(confirmPassword)
            ) {
                Toast.makeText(
                    applicationContext,
                    "All fields are required",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Check if the password and confirm password match
            if (password != confirmPassword) {
                Toast.makeText(
                    applicationContext,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Create a new user with email and password
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Get the newly registered user's ID
                        val userId = auth.currentUser?.uid ?: ""

                        // Create a new user document in Firestore with the user's first name and last name
                        val user = hashMapOf(
                            "first_name" to firstName,
                            "last_name" to lastName
                        )
                        db.collection("users").document(userId).set(user)
                            .addOnSuccessListener {
                                // Display a success message
                                Toast.makeText(
                                    applicationContext,
                                    "Registration successful",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Log in the newly registered user
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { loginTask ->
                                        if (loginTask.isSuccessful) {
                                            // Start the main activity
                                            val intent = Intent(this,FirstLogin::class.java)
                                            startActivity(intent)
                                            finish()                                        } else {
                                            // Display an error message
                                            Toast.makeText(
                                                applicationContext,
                                                "Error: ${loginTask.exception?.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                    }
                }
        }
    }
}