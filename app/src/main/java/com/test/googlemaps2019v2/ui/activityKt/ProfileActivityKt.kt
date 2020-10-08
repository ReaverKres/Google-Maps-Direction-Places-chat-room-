package com.test.googlemaps2019v2.ui.activityKt

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.test.googlemaps2019v2.R
import com.test.googlemaps2019v2.models.user.UserClient
import com.test.googlemaps2019v2.ui.fragment.UserListFragmentKt
import de.hdodenhof.circleimageview.CircleImageView


class ProfileActivityKt : AppCompatActivity() {

    //widgets
    private val mAvatarImage: CircleImageView? = null

    //vars
    private val mUserListFragment: UserListFragmentKt? = null

    companion object {
        private const val TAG = "ProfileActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        retrieveProfileImage()
    }

    private fun retrieveProfileImage() {
        val requestOptions = RequestOptions()
                .error(R.drawable.cwm_logo)
                .placeholder(R.drawable.cwm_logo)

        var avatar = 0
        try {
            avatar = (applicationContext as UserClient).user.avatar.toInt()
        } catch (e: NumberFormatException) {
            Log.e(Companion.TAG, "retrieveProfileImage: no avatar image. Setting default. " + e.message)
        }

        mAvatarImage?.let {
            Glide.with(this@ProfileActivityKt)
                .setDefaultRequestOptions(requestOptions)
                .load(avatar)
                .into(it)
        }
    }

    fun onImageSelected(resource: Int) {

        // remove the image selector fragment
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_down, R.anim.slide_out_down, R.anim.slide_out_up)
                .remove(mUserListFragment!!)
                .commit()

        // display the image
        val requestOptions = RequestOptions()
                .placeholder(R.drawable.cwm_logo)
                .error(R.drawable.cwm_logo)
        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(resource)
                .into(mAvatarImage!!)

        // update the client and database
        val user = (applicationContext as UserClient).user
        user.avatar = resource.toString()
        FirebaseFirestore.getInstance()
                .collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().uid!!)
                .set(user)
    }
}