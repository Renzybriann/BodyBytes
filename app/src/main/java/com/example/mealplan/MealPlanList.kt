package com.example.mealplan

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class MealPlanList : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_plan_list)
        supportActionBar?.hide()

        listView = findViewById(R.id.firstListView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        listView.adapter = adapter

        firestore = FirebaseFirestore.getInstance()

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUserUid = currentUser?.uid ?: ""

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedItem = adapter.getItem(position)
            if (selectedItem != null) {
                val documentId = getDocumentId(selectedItem)
                selectItem(documentId)
                moveToNextActivity()
            }
        }

        val mealPlanCollection = firestore.collection("users").document(currentUserUid).collection("Mealplan")

        mealPlanCollection.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                // Handle any errors
                return@addSnapshotListener
            }

            adapter.clear()

            for (document in querySnapshot!!) {
                val name = document.getString("name")
                val documentId = document.id
                if (name != null) {
                    adapter.add(getFormattedName(name, documentId))
                }
            }

            adapter.notifyDataSetChanged()
        }
    }

    private fun selectItem(documentId: String) {
        val userDocRef = firestore.collection("users").document(currentUserUid)
        userDocRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot? = task.result
                if (document != null && document.exists()) {
                    userDocRef.update("Select", documentId)
                        .addOnSuccessListener {
                            // Selected document ID is updated in the "Select" field under the user's UID
                        }
                        .addOnFailureListener { exception ->
                            // Handle any errors
                        }
                }
            }
        }
    }

    private fun moveToNextActivity() {
        val intent = Intent(this, MealPlanInfo::class.java)
        startActivity(intent)
        finish()
    }

    private fun getDocumentId(formattedName: String): String {
        return formattedName.substringAfterLast(" [").removeSuffix("]")
    }

    private fun getFormattedName(name: String, documentId: String): String {
        return "$name [$documentId]"
    }
}
