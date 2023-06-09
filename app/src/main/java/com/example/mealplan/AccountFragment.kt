package com.example.mealplan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private lateinit var db: FirebaseFirestore
private lateinit var auth: FirebaseAuth
private lateinit var firstNameTextView: TextView
//private lateinit var lastNameTextView: TextView
private lateinit var ageTextView: TextView
private lateinit var birthdayDayTextView: TextView
private lateinit var birthdayMonthTextView: TextView
private lateinit var birthdayYearTextView: TextView

private lateinit var bmiTextView: TextView
private lateinit var heightTextView: TextView
private lateinit var weightTextView: TextView
private lateinit var logoutButton: Button

class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vieww = inflater.inflate(R.layout.fragment_account, container, false)


        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        firstNameTextView = vieww.findViewById(R.id.FIRSTNAMES1)
//        lastNameTextView = vieww.findViewById(R.id.LASTNAMES2)
        ageTextView = vieww.findViewById(R.id.AGE1)
        birthdayDayTextView = vieww.findViewById(R.id.DAYS1_1)
        birthdayMonthTextView = vieww.findViewById(R.id.MONTHS1)
        birthdayYearTextView = vieww.findViewById(R.id.YEARS1)
        heightTextView = vieww.findViewById(R.id.HEIGHT)
        weightTextView = vieww.findViewById(R.id.WEIGHT)

        logoutButton = vieww.findViewById(R.id.LOGOUT)
        logoutButton.setOnClickListener {
            // Sign out the user and send them back to the login activity
            auth.signOut()
            val intent = Intent(requireActivity(), Login::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid

            // Retrieve first name and last name from Firestore
            db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val firstName = document.getString("first_name")
                        val lastName = document.getString("last_name")

                        // Set first name and last name to a single TextView
                        val nameTextView = vieww.findViewById<TextView>(R.id.FULLNAMES)
                        nameTextView.text = "$firstName $lastName"

                        // Set first name and last name to separate TextViews
                        firstNameTextView.text = firstName
//                        lastNameTextView.text = lastName

                        // Retrieve user info from the subcollection "userinfos"
                        db.collection("users")
                            .document(uid)
                            .collection("userinfos")
                            .document("agedata")
                            .get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    val age = document.getLong("age")?.toInt()
                                    val birthdayDay = document.getLong("birthdayDay")?.toInt()
                                    val birthdayMonth = document.getLong("birthdayMonth")?.toInt()
                                    val birthdayYear = document.getLong("birthdayYear")?.toInt()

                                    // Set age, birthday day, month, and year to separate TextViews
                                    ageTextView.text = age?.toString() ?: "N/A"
                                    birthdayDayTextView.text = birthdayDay?.toString() ?: "N/A"
                                    birthdayMonthTextView.text = birthdayMonth?.toString() ?: "N/A"
                                    birthdayYearTextView.text = birthdayYear?.toString() ?: "N/A"
                                } else {
                                    Log.d(Settings.TAG, "No such document")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d(Settings.TAG, "get failed with ", exception)
                            }

                        // Retrieve BMI data from the "bmidata" document
                        db.collection("users")
                            .document(uid)
                            .collection("userinfos")
                            .document("bmidata")
                            .get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    val bmi = document.getDouble("bmi")?.toString() ?: "N/A"
                                    val height = document.getDouble("height")?.toString() ?: "N/A"
                                    val weight = document.getDouble("weight")?.toString() ?: "N/A"

                                    // Set BMI, height, and weight to separate TextViews
                                    heightTextView.text = "$height m"
                                    weightTextView.text = "$weight kg"
                                } else {
                                    Log.d(Settings.TAG, "No such document")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d(Settings.TAG, "get failed with ", exception)
                            }
                    } else {
                        Log.d(Settings.TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(Settings.TAG, "get failed with ", exception)
                }
        }




        return vieww
    }

    companion object {
        const val TAG = "ProfileActivity"
    }
}