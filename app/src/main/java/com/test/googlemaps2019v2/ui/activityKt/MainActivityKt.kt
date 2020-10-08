package com.test.googlemaps2019v2.ui.activityKt

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.test.googlemaps2019v2.R
import com.test.googlemaps2019v2.ui.activity.LoginActivity

class MainActivityKt : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_2020)

        navController = Navigation.findNavController(this, R.id.main_nav_host_fragment);
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bnvMain)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.action_sign_out -> {
                        signOut()
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.action_chats -> {
                        navController.navigate(R.id.chatroomActivity)
                        return@OnNavigationItemSelectedListener true
                    }

                    R.id.action_profile -> {
                        navController.navigate(R.id.profileActivity)
                        return@OnNavigationItemSelectedListener true
                    }

                    R.id.action_about -> {
                        navController.navigate(R.id.aboutActivity)
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}