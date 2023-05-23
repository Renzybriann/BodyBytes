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

import android.util.Log

class Login : AppCompatActivity() {

    // Declare instance variables
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var showPasswordCheckBox: CheckBox
    private lateinit var rememberMeCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        // Check if user is already logged in
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
            finish()
        }


        setContentView(R.layout.activity_login)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        val mAuth = FirebaseAuth.getInstance()

        // Assign views to instance variables
        emailEditText = findViewById(R.id.emailInput)
        passwordEditText = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox)
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox)

        // Set up listener for the "Show password" checkbox
        showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // If the checkbox is checked, show the password
                passwordEditText.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                // If the checkbox is unchecked, hide the password
                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        // Set up listener for the "Login" button
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty()) {
                // Show an error message if email is empty
                emailEditText.error = "Email is required!"
                emailEditText.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                // Show an error message if password is empty
                passwordEditText.error = "Password is required!"
                passwordEditText.requestFocus()
                return@setOnClickListener
            }

            // Attempt to sign in the user with the given email and password
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // If the login is successful, go to the main menu activity
                        val intent = Intent(this, MainMenu::class.java)
                        startActivity(intent)
                        if (rememberMeCheckBox.isChecked) {
                            // If the "Remember me" checkbox is checked, persist the user's login state
                            auth.currentUser?.let { user ->
                                val editor =
                                    getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit()
                                editor.putString("user_id", user.uid)
                                editor.apply()
                            }

                        }
                        finish()
                    } else {
                        // If the login fails, show an error message
                        Toast.makeText(baseContext, "Authentication failed!", Toast.LENGTH_SHORT).show()
                    }
                }
        }


        val buttonClick = findViewById<Button>(R.id.registerButton)
        buttonClick.setOnClickListener {
            val intent = Intent(this, RegistrationPage::class.java)
            startActivity(intent)
        }
    }
}
