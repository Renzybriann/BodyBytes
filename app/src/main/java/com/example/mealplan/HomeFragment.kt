package com.example.mealplan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserUid: String
    private lateinit var createplan: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val vieww = inflater.inflate(R.layout.fragment_home, container, false)


        listView = vieww.findViewById(R.id.firstListView)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1)
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

        createplan = vieww.findViewById(R.id.create_meal_plan_button)
        createplan.setOnClickListener {
            loadFragment(MealListFragment())
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

        return vieww
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
        val intent = Intent(activity, MealPlanInfo::class.java)
        activity?.startActivity(intent)
        activity?.finish()
    }

    private fun getDocumentId(formattedName: String): String {
        return formattedName.substringAfterLast(" [").removeSuffix("]")
    }

    private fun getFormattedName(name: String, documentId: String): String {
        return "$name [$documentId]"
    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container,fragment)
        transaction.commit()
    }

}