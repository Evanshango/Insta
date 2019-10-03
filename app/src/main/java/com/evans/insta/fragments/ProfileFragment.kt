package com.evans.insta.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.evans.insta.AccountActivity

import com.evans.insta.R
import com.evans.insta.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {

    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)

        pref?.let {
            this.profileId = it.getString("profileId", "none")!!
        }

        if (profileId == firebaseUser.uid) {
            view.btn_edit_profile.text = getString(R.string.edit_profile)
        } else if (profileId != firebaseUser.uid) {
            checkFollowAndFollowingButtonStatus()
        }

        view.btn_edit_profile.setOnClickListener {
            startActivity(Intent(context, AccountActivity::class.java))
        }

        getFollowers()
        getFollowing()
        userInfo()

        return view
    }

    private fun checkFollowAndFollowingButtonStatus() {

        firebaseUser.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow")
                .child(it1)
                .child("Following")
        }.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(profileId).exists()) {
                    view?.btn_edit_profile?.text = getString(R.string.following)
                } else {
                    view?.btn_edit_profile?.text = getString(R.string.follow)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                val msg = p0.message
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow")
            .child(profileId)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    view?.total_followers?.text = p0.childrenCount.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                val error = p0.message
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getFollowing() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow")
            .child(profileId)
            .child("Following")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    view?.total_following?.text = p0.childrenCount.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                val error = p0.message
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun userInfo() {
        val userRef = FirebaseDatabase
            .getInstance()
            .reference
            .child("users")
            .child(profileId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user = p0.getValue<User>(User::class.java)
                    Glide.with(this@ProfileFragment)
                        .load(user!!.getImage())
                        .into(view?.profile_image as CircleImageView)

                    view?.profile_fragment_username?.text = user.getUserName()
                    view?.profile_fragment_full_name?.text = user.getFullName()
                    view?.profile_fragment_bio?.text = user.getBio()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context, p0.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUserId() {
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onStop() {
        super.onStop()
        saveUserId()
    }

    override fun onPause() {
        super.onPause()
        saveUserId()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveUserId()
    }
}
