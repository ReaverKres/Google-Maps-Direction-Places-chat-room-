package com.test.googlemaps2019v2.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.test.googlemaps2019v2.IProfile
import com.test.googlemaps2019v2.R
import com.test.googlemaps2019v2.ui.adapters.ImageListRecyclerAdapter
import com.test.googlemaps2019v2.ui.adapters.ImageListRecyclerAdapter.ImageListRecyclerClickListener
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [UserListFragmentKt.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserListFragmentKt : Fragment(), ImageListRecyclerClickListener {
    //widgets
    private var mRecyclerView: RecyclerView? = null

    //vars
    private var mImageResources = ArrayList<Int>()
    private var mIProfile: IProfile? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_image_list, container, false)
        mRecyclerView = view.findViewById(R.id.image_list_recyclerview)
        imageResouces
        initRecyclerview()
        return view
    }

    private val imageResouces: Unit
        get() {
            mImageResources = arrayListOf(R.drawable.cwm_logo, R.drawable.cartman_cop,
                                          R.drawable.eric_cartman, R.drawable.ike, R.drawable.kyle,
                                          R.drawable.satan, R.drawable.chef, R.drawable.tweek)
        }

    private fun initRecyclerview() {
        val mAdapter = ImageListRecyclerAdapter(activity, mImageResources, this)
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL)
        mRecyclerView!!.layoutManager = staggeredGridLayoutManager
        mRecyclerView!!.adapter = mAdapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIProfile = activity as IProfile?
    }

    override fun onImageSelected(position: Int) {
        mIProfile!!.onImageSelected(mImageResources[position])
    }

    companion object {
        private const val NUM_COLUMNS = 2
        fun newInstance(): UserListFragmentKt {
            return UserListFragmentKt()
        }
    }
}