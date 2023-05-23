package com.example.mealplan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
class  MainMenu : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    lateinit var bottomNav : BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        loadFragment(HomeFragment())
        bottomNav = findViewById(R.id.bottomNavigationView) as BottomNavigationView
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.chart -> {
                    loadFragment(ChartFragment())
                    true
                }
                R.id.account -> {
                    loadFragment(AccountFragment())
                    true
                }

                else -> {throw IllegalStateException("Fragment is not loading")}
            }
        }
        // Initialize logout button and set listener
//        val buttonClick2 = findViewById<Button>(R.id.SETTERS)
//        buttonClick2.setOnClickListener {
//            val intent3 = Intent(this, Settings::class.java)
//            startActivity(intent3)
//        }
//        val buttonClick3 = findViewById<Button>(R.id.TestButton)
//        buttonClick3.setOnClickListener {
//            val intent3 = Intent(this, MealList::class.java)
//            startActivity(intent3)
//        }
//
//
//
//        val buttonClick = findViewById<Button>(R.id.mealButton1)
//        buttonClick.setOnClickListener {
//            val intent = Intent(this, MealPlanList::class.java)
//            startActivity(intent)
//        }
//
//
//        val buttonClick1 = findViewById<Button>(R.id.WeightButton)
//        buttonClick1.setOnClickListener {
//            val intent2 = Intent(this, WeightCompare::class.java)
//            startActivity(intent2)
//        }
    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container,fragment)
        transaction.commit()
    }
}