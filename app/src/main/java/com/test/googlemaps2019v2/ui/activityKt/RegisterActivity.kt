package com.test.googlemaps2019v2.ui.activityKt

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.test.googlemaps2019v2.R
import com.test.googlemaps2019v2.models.user.User
import com.test.googlemaps2019v2.util.Check
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val TAG = "RegisterActivity"
    }

    //vars
    private var mDb: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_register.setOnClickListener(this)
        mDb = FirebaseFirestore.getInstance()
        hideSoftKeyboard()
    }

    /**
     * Register a new email and password to Firebase Authentication
     * @param email
     * @param password
     */
    fun registerNewEmail(email: String, password: String?) {
        showDialog()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password!!)
                .addOnCompleteListener { task ->
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful)
                    if (task.isSuccessful) {
                        Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().currentUser!!.uid)

                        //insert some default data
                        val user = User()
                        user.email = email
                        user.username = email.substring(0, email.indexOf("@"))
                        user.user_id = FirebaseAuth.getInstance().uid
                        val settings = FirebaseFirestoreSettings.Builder()
                                .setTimestampsInSnapshotsEnabled(true)
                                .build()
                        mDb!!.firestoreSettings = settings
                        val newUserRef = mDb!!
                                .collection(getString(R.string.collection_users))
                                .document(FirebaseAuth.getInstance().uid!!)
                        newUserRef.set(user).addOnCompleteListener { task ->
                            hideDialog()
                            if (task.isSuccessful) {
                                redirectLoginScreen()
                            } else {
                                val parentLayout = findViewById<View>(android.R.id.content)
                                Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        val parentLayout = findViewById<View>(android.R.id.content)
                        Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show()
                        hideDialog()
                    }

                }
    }

    /**
     * Redirects the user to the login screen
     */
    private fun redirectLoginScreen() {
        Log.d(Companion.TAG, "redirectLoginScreen: redirecting to login screen.")
        val intent = Intent(this@RegisterActivity, LoginActivityKt::class.java)
        startActivity(intent)
        finish()
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

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_register -> {
                Log.d(Companion.TAG, "onClick: attempting to register.")

                //check for null valued EditText fields
                if (!TextUtils.isEmpty(email.text.toString())
                        && !TextUtils.isEmpty(password.text.toString())
                        && !TextUtils.isEmpty(confirm_password.text.toString())) {

                    //check if passwords match
                    if (Check.doStringsMatch(password!!.text.toString(), confirm_password!!.text.toString())) {

                        //Initiate registration task
                        registerNewEmail(email!!.text.toString(), password!!.text.toString())
                    } else {
                        Toast.makeText(this@RegisterActivity, "Passwords do not Match", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "You must fill out all the fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}