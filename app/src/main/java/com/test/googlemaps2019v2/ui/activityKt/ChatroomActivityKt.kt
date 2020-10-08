package com.test.googlemaps2019v2.ui.activityKt

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.test.googlemaps2019v2.R
import com.test.googlemaps2019v2.models.chat.ChatMessage
import com.test.googlemaps2019v2.models.chat.Chatroom
import com.test.googlemaps2019v2.models.event.Event
import com.test.googlemaps2019v2.models.event.EventLocation
import com.test.googlemaps2019v2.models.user.User
import com.test.googlemaps2019v2.models.user.UserClient
import com.test.googlemaps2019v2.models.user.UserLocation
import com.test.googlemaps2019v2.ui.activity.MainActivity
import com.test.googlemaps2019v2.ui.adapters.ChatMessageRecyclerAdapter
import com.test.googlemaps2019v2.ui.fragment.MapFragment
import java.util.*


class ChatroomActivity : AppCompatActivity(), View.OnClickListener {

    //widgets
    private var mChatroom: Chatroom? = null
    private var mMessage: EditText? = null

    //vars
    private var mChatMessageEventListener: ListenerRegistration? = null
    private var mEventListListener: ListenerRegistration? = null
    private var mChatMessageRecyclerView: RecyclerView? = null
    private var mChatMessageRecyclerAdapter: ChatMessageRecyclerAdapter? = null
    private var mDb: FirebaseFirestore? = null
    private val mMessages = ArrayList<ChatMessage>()
    private val mMessageIds: MutableSet<String> = HashSet()
    private var mUserList = ArrayList<User>()
    private var mEventList = ArrayList<Event>()
    private val mUserLocations = ArrayList<UserLocation>()
    private var mEventLocations = ArrayList<EventLocation>()
    private val mImageResources = ArrayList<Int>()

    companion object {
        private const val TAG = "ChatroomActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)

        mMessage = findViewById(R.id.input_message)
        mChatMessageRecyclerView = findViewById(R.id.chatmessage_recycler_view)
        findViewById<View>(R.id.checkmark).setOnClickListener(this)
        mDb = FirebaseFirestore.getInstance()

        incomingIntent
        initChatroomRecyclerView()
        chatroomUsers
        chatroomEvents
        chatroomEventsLocation
        imageResouces

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavView_Bar)
        val navMenu = bottomNavigationView.menu
        val menuItem = navMenu.getItem(1)
        menuItem.title = mChatroom!!.title
        menuItem.isChecked = false
        val spanString = SpannableString(menuItem.title.toString())
        val end = spanString.length
        spanString.setSpan(RelativeSizeSpan(2.0f), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        menuItem.title = spanString
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                android.R.id.home -> {
                    val fragment = supportFragmentManager.findFragmentByTag(getString(R.string.fragment_user_list)) as MapFragment?
                    if (fragment != null) {
                        if (fragment.isVisible) {
                            supportFragmentManager.popBackStack()
                            return@OnNavigationItemSelectedListener true
                        }
                    }
                    finish()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_chatroom_user_list -> {
                    inflateUserListFragment()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_chatroom_leave -> {
                    leaveChatroom()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    private fun getUserLocation(user: User) {
        val locationsRef = mDb!!
                .collection(getString(R.string.collection_user_locations))
                .document(user.user_id)
        locationsRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result!!.toObject(UserLocation::class.java) != null) {
                    task.result!!.toObject(UserLocation::class.java)?.let { mUserLocations.add(it) }
                }
            }
        }
    }

    private val chatMessages: Unit
        get() {
            val messagesRef = mDb!!
                    .collection(getString(R.string.collection_chatrooms))
                    .document(mChatroom!!.chatroom_id)
                    .collection(getString(R.string.collection_chat_messages))
            mChatMessageEventListener = messagesRef
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener(EventListener { queryDocumentSnapshots, e ->
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e)
                            return@EventListener
                        }
                        if (queryDocumentSnapshots != null) {
                            for (doc in queryDocumentSnapshots) {
                                val message = doc.toObject(ChatMessage::class.java)
                                if (!mMessageIds.contains(message.message_id)) {
                                    mMessageIds.add(message.message_id)
                                    mMessages.add(message)
                                    mChatMessageRecyclerView!!.smoothScrollToPosition(mMessages.size - 1)
                                }
                            }
                            mChatMessageRecyclerAdapter!!.notifyDataSetChanged()
                        }
                    })
        }

    // Clear the list and add all the users again
    private val chatroomUsers: Unit
        get() {
            val usersRef = mDb!!
                    .collection(getString(R.string.collection_chatrooms))
                    .document(mChatroom!!.chatroom_id)
                    .collection(getString(R.string.collection_chatroom_user_list))
            mEventListListener = usersRef
                    .addSnapshotListener(EventListener { queryDocumentSnapshots, e ->
                        if (e != null) {
                            Log.e(TAG, "(getChatroomUsers)onEvent: Listen failed.", e)
                            return@EventListener
                        }
                        if (queryDocumentSnapshots != null) {
                            // Clear the list and add all the users again
                            mUserList.clear()
                            mUserList = ArrayList()
                            for (doc in queryDocumentSnapshots) {
                                val user = doc.toObject(User::class.java)
                                mUserList.add(user)
                                getUserLocation(user)
                            }
                            Log.d(TAG, "onEvent: user list size: " + mUserList.size)
                        }
                    })
        }

    // Clear the list and add all the users again
    private val chatroomEvents: Unit
        get() {
            val eventsRef = mDb!!
                    .collection(getString(R.string.collection_chatrooms))
                    .document(mChatroom!!.chatroom_id)
                    .collection(getString(R.string.collection_event))
            mEventListListener = eventsRef
                    .addSnapshotListener(EventListener { queryDocumentSnapshots, e ->
                        if (e != null) {
                            Log.e(TAG, "(getChatroomEvents) onEvent: Listen failed.", e)
                            return@EventListener
                        }
                        if (queryDocumentSnapshots != null) {
                            // Clear the list and add all the users again
                            mEventList.clear()
                            mEventList = ArrayList()
                            for (doc in queryDocumentSnapshots) {
                                val event = doc.toObject(Event::class.java)
                                mEventList.add(event)
                            }
                            Log.d(TAG, "onEvent: user list size: " + mEventList.size)
                        }
                    })
        }

    // Clear the list and add all the events again
    private val chatroomEventsLocation: Unit
        get() {
            val eventsRef = mDb!!
                    .collection(getString(R.string.collection_chatrooms))
                    .document(mChatroom!!.chatroom_id)
                    .collection(getString(R.string.collection_event_locations))
            mEventListListener = eventsRef
                    .addSnapshotListener(EventListener { queryDocumentSnapshots, e ->
                        if (e != null) {
                            Log.e(TAG, "(getChatroomEvents) onEvent: Listen failed.", e)
                            return@EventListener
                        }
                        if (queryDocumentSnapshots != null) {
                            // Clear the list and add all the events again
                            mEventLocations.clear()
                            mEventLocations = ArrayList()
                            for (doc in queryDocumentSnapshots) {
                                val eventLocation = doc.toObject(EventLocation::class.java)
                                mEventLocations.add(eventLocation)
                            }
                            Log.d(TAG, "onEvent: user list size: " + mEventList.size)
                        }
                    })
        }

    private fun initChatroomRecyclerView() {
        mChatMessageRecyclerAdapter = ChatMessageRecyclerAdapter(mMessages, ArrayList(), this)
        mChatMessageRecyclerView!!.adapter = mChatMessageRecyclerAdapter
        mChatMessageRecyclerView!!.layoutManager = LinearLayoutManager(this)
        mChatMessageRecyclerView!!.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                mChatMessageRecyclerView!!.postDelayed({
                    if (mMessages.size > 0) {
                        mChatMessageRecyclerView!!.smoothScrollToPosition(
                                mChatMessageRecyclerView!!.adapter!!.itemCount - 1)
                    }
                }, 100)
            }
        }
    }

    private fun insertNewMessage() {
        var message = mMessage!!.text.toString()
        if (message != "") {
            message = message.replace(System.getProperty("line.separator")!!.toRegex(), "")
            val newMessageDoc = mDb!!
                    .collection(getString(R.string.collection_chatrooms))
                    .document(mChatroom!!.chatroom_id)
                    .collection(getString(R.string.collection_chat_messages))
                    .document()
            val newChatMessage = ChatMessage()
            newChatMessage.message = message
            newChatMessage.message_id = newMessageDoc.id
            val user = (applicationContext as UserClient).user
            Log.d(TAG, "insertNewMessage: retrieved user client: $user")
            newChatMessage.user = user
            newMessageDoc.set(newChatMessage).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    clearMessage()
                } else {
                    val parentLayout = findViewById<View>(android.R.id.content)
                    Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun clearMessage() {
        mMessage!!.setText("")
    }

    private fun inflateUserListFragment() {
        hideSoftKeyboard()
        val fragment = MapFragment.newInstance()
        val bundle = Bundle()
        bundle.putParcelableArrayList(getString(R.string.intent_user_list), mUserList)
        bundle.putParcelableArrayList(getString(R.string.intent_user_locations), mUserLocations)
        bundle.putParcelableArrayList(getString(R.string.intent_event_list), mEventList)
        bundle.putParcelableArrayList(getString(R.string.intent_event_locations), mEventLocations)
        bundle.putString("chat_id", mChatroom!!.chatroom_id)
        fragment.arguments = bundle
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
        transaction.replace(R.id.user_list_container, fragment, getString(R.string.fragment_user_list))
        transaction.addToBackStack(getString(R.string.fragment_user_list))
        transaction.commit()
    }

    private fun hideSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private val incomingIntent: Unit
        get() {
            if (intent.hasExtra(getString(R.string.intent_chatroom))) {
                mChatroom = intent.getParcelableExtra(getString(R.string.intent_chatroom))
                joinChatroom()
            }
        }

    private fun leaveChatroom() {
        val joinChatroomRef = mDb!!
                .collection(getString(R.string.collection_chatrooms))
                .document(mChatroom!!.chatroom_id)
                .collection(getString(R.string.collection_chatroom_user_list))
                .document(FirebaseAuth.getInstance().uid!!)
        joinChatroomRef.delete()
        val intent = Intent(this@ChatroomActivity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun joinChatroom() {
        val joinChatroomRef = mDb!!
                .collection(getString(R.string.collection_chatrooms))
                .document(mChatroom!!.chatroom_id)
                .collection(getString(R.string.collection_chatroom_user_list))
                .document(FirebaseAuth.getInstance().uid!!)
        val user = (applicationContext as UserClient).user
        joinChatroomRef.set(user) // Don't care about listening for completion.
    }

    override fun onResume() {
        super.onResume()
        chatMessages
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mChatMessageEventListener != null) {
            mChatMessageEventListener!!.remove()
        }
        if (mEventListListener != null) {
            mEventListListener!!.remove()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.chatroom_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val fragment = supportFragmentManager.findFragmentByTag(getString(R.string.fragment_user_list)) as MapFragment?
                if (fragment != null) {
                    if (fragment.isVisible) {
                        supportFragmentManager.popBackStack()
                        return true
                    }
                }
                finish()
                true
            }
            R.id.action_chatroom_user_list -> {
                inflateUserListFragment()
                true
            }
            R.id.action_chatroom_leave -> {
                leaveChatroom()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.checkmark -> {
                insertNewMessage()
            }
        }
    }

    private val imageResouces: Unit
        get() {
            mImageResources.add(R.drawable.cwm_logo)
            mImageResources.add(R.drawable.cartman_cop)
            mImageResources.add(R.drawable.eric_cartman)
            mImageResources.add(R.drawable.ike)
            mImageResources.add(R.drawable.kyle)
            mImageResources.add(R.drawable.satan)
            mImageResources.add(R.drawable.chef)
            mImageResources.add(R.drawable.tweek)
        }


}