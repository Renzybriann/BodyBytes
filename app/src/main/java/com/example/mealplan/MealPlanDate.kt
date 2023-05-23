package com.example.mealplan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.content.Intent

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

public class MealPlanDate : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var dateInput: EditText
    private lateinit var monthInput: EditText
    private lateinit var yearInput: EditText
    private lateinit var nameInput: EditText
    private lateinit var submitButton: Button

    private var meals1 = ArrayList<String>()  // Hold the meals of the meal plans
    private lateinit var mealAdapter1: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_plan_date)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        dateInput = findViewById(R.id.dateInput)
        monthInput = findViewById(R.id.monthInput)
        yearInput = findViewById(R.id.yearInput)
        nameInput = findViewById(R.id.nameInput)
        submitButton = findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            val date = dateInput.text.toString().trim()
            val month = monthInput.text.toString().trim()
            val year = yearInput.text.toString().trim()
            val name = nameInput.text.toString().trim()

            if (validateDate(date) && validateMonth(month) && validateYear(year) && validateName(name)) {
                val currentUserUid = firebaseAuth.currentUser?.uid

                if (currentUserUid != null) {
                    val uid = "$date$month$year"

                    val mealPlanData = hashMapOf(
                        "date" to date,
                        "month" to month,
                        "year" to year,
                        "name" to name,
                        "Name" to name // Save the name into "Name" field
                    )

                    val mealPlanCollection = firestore.collection("users")
                        .document(currentUserUid)
                        .collection("Mealplan")

                    mealPlanCollection.document(uid)
                        .set(mealPlanData)
                        .addOnSuccessListener {
                            createSubcollection(currentUserUid, uid, name)
                        }
                }
            }
        }
    }

    private fun validateDate(date: String): Boolean {
        return date.isNotEmpty()
    }

    private fun validateMonth(month: String): Boolean {
        val validMonths = listOf(
            "January", "February", "March", "April",
            "May", "June", "July", "August",
            "September", "October", "November", "December"
        )
        return validMonths.contains(month)
    }

    private fun validateYear(year: String): Boolean {
        return year.isNotEmpty()
    }

    private fun validateName(name: String): Boolean {
        return name.isNotEmpty()
    }

    private fun createSubcollection(userId: String, mealPlanId: String, name: String) {
        val subcollectionData = hashMapOf<String, Any>() // Add any additional data for the DATA document

        val subcollectionRef = firestore.collection("users")
            .document(userId)
            .collection("Mealplan")
            .document(mealPlanId)
            .collection(name)

        // Create DATA document
        subcollectionRef.document("DATA").set(subcollectionData)
            .addOnSuccessListener {
                copyTemporaryBreakfast(userId, mealPlanId, name)
            }
    }

    private fun copyTemporaryBreakfast(userId: String, mealPlanId: String, name: String) {
        val temporaryBreakfastRef = firestore.collection("users")
            .document(userId)
            .collection("TemporaryBreakfast")

        temporaryBreakfastRef.get()
            .addOnSuccessListener { snapshot ->
                val batch = firestore.batch()

                for (document in snapshot.documents) {
                    val documentData = document.data
                    if (documentData != null) {
                        val documentId = document.id
                        val newDocumentRef = firestore.collection("users")
                            .document(userId)
                            .collection("Mealplan")
                            .document(mealPlanId)
                            .collection(name)
                            .document("1_Breakfast-$documentId") // Add "1_" to the UID

                        // Add the "Time" field with the value "Breakfast" to the copied document
                        documentData["Time"] = "Breakfast"
                        batch.set(newDocumentRef, documentData)

                        // Delete the document from TemporaryBreakfast collection
                        val temporaryBreakfastDocRef = temporaryBreakfastRef.document(documentId)
                        batch.delete(temporaryBreakfastDocRef)
                    }
                }

                batch.commit()
                    .addOnSuccessListener {
                        copyTemporaryLunch(userId, mealPlanId, name)
                    }
            }
    }

    private fun copyTemporaryLunch(userId: String, mealPlanId: String, name: String) {
        val temporaryLunchRef = firestore.collection("users")
            .document(userId)
            .collection("TemporaryLunch")

        temporaryLunchRef.get()
            .addOnSuccessListener { snapshot ->
                val batch = firestore.batch()

                for (document in snapshot.documents) {
                    val documentData = document.data
                    if (documentData != null) {
                        val documentId = document.id
                        val newDocumentRef = firestore.collection("users")
                            .document(userId)
                            .collection("Mealplan")
                            .document(mealPlanId)
                            .collection(name)
                            .document("2_Lunch-$documentId") // Add "2_" to the UID

                        // Add the "Time" field with the value "Lunch" to the copied document
                        documentData["Time"] = "Lunch"
                        batch.set(newDocumentRef, documentData)

                        // Delete the document from TemporaryLunch collection
                        val temporaryLunchDocRef = temporaryLunchRef.document(documentId)
                        batch.delete(temporaryLunchDocRef)
                    }
                }

                batch.commit()
                    .addOnSuccessListener {
                        copyTemporaryDinner(userId, mealPlanId, name)
                    }
            }
    }

    private fun copyTemporaryDinner(userId: String, mealPlanId: String, name: String) {
        val temporaryDinnerRef = firestore.collection("users")
            .document(userId)
            .collection("TemporaryDinner")

        temporaryDinnerRef.get()
            .addOnSuccessListener { snapshot ->
                val batch = firestore.batch()

                for (document in snapshot.documents) {
                    val documentData = document.data
                    if (documentData != null) {
                        val documentId = document.id
                        val newDocumentRef = firestore.collection("users")
                            .document(userId)
                            .collection("Mealplan")
                            .document(mealPlanId)
                            .collection(name)
                            .document("3_Dinner-$documentId") // Add "3_" to the UID

                        // Add the "Time" field with the value "Dinner" to the copied document
                        documentData["Time"] = "Dinner"
                        batch.set(newDocumentRef, documentData)

                        // Delete the document from TemporaryDinner collection
                        val temporaryDinnerDocRef = temporaryDinnerRef.document(documentId)
                        batch.delete(temporaryDinnerDocRef)
                    }
                }

                batch.commit()
                    .addOnSuccessListener {
                        moveToNextActivity()
                    }
            }
    }


    private fun moveToNextActivity() {
        val intent = Intent(this, MealList::class.java)
        startActivity(intent)
    }
}

