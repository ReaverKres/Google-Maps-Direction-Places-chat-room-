package com.test.googlemaps2019v2.ui.activityKt

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.test.googlemaps2019v2.R
import com.test.googlemaps2019v2.models.user.User
import com.test.googlemaps2019v2.models.user.UserClient
import com.test.googlemaps2019v2.ui.activity.RegisterActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivityKt : AppCompatActivity(), View.OnClickListener {

    private val TAG = "LoginActivity"

    //Firebase
    private var mAuthListener: AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email_sign_in_button.setOnClickListener(this)
        link_register.setOnClickListener(this)
        setupFirebaseAuth()
        hideSoftKeyboard()
    }

    private fun showDialog() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideDialog() {
        if (progressBar.visibility == View.VISIBLE) {
            progressBar.visibility = View.INVISIBLE
        }
    }

    private fun hideSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    /*
        ----------------------------- Firebase setup ---------------------------------
     */
    private fun setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.")
        mAuthListener = AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.uid)
                Toast.makeText(this@LoginActivityKt, "Authenticated with: " + user.email, Toast.LENGTH_SHORT).show()
                val db = FirebaseFirestore.getInstance()
                val settings = FirebaseFirestoreSettings.Builder()
                        .setTimestampsInSnapshotsEnabled(true)
                        .build()
                db.firestoreSettings = settings
                val userRef = db.collection(getString(R.string.collection_users))
                        .document(user.uid)
                userRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "onComplete: successfully set the user client.")
                        val user = task.result!!.toObject(User::class.java)
                        (applicationContext as UserClient).user = user
                    }
                }
                val intent = Intent(this@LoginActivityKt, MainActivityKt::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out")
            }
            // ...
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener!!)
        }
    }

    private fun signIn() {
        //check if the fields are filled out
        if (!TextUtils.isEmpty(email.text.toString())
                && !TextUtils.isEmpty(password.text.toString())) {
            Log.d(TAG, "onClick: attempting to authenticate.")
            showDialog()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email.text.toString(),
                    password.text.toString())
                    .addOnCompleteListener { hideDialog() }.addOnFailureListener {
                        Toast.makeText(this@LoginActivityKt, "Authentication Failed", Toast.LENGTH_SHORT).show()
                        hideDialog()
                    }
        } else {
            Toast.makeText(this@LoginActivityKt, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.link_register -> {
                val intent = Intent(this@LoginActivityKt, RegisterActivity::class.java)
                startActivity(intent)
            }
            R.id.email_sign_in_button -> {
                signIn()
            }
        }
    }
}