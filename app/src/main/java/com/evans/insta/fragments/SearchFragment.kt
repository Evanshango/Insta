package com.evans.insta.fragments


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.evans.insta.R
import com.evans.insta.adapters.UserAdapter
import com.evans.insta.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<User>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.search_recycler_view)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()

        userAdapter = context?.let {
            UserAdapter(it, mUser as ArrayList<User>, true)
        }
        recyclerView?.adapter = userAdapter

        view.search_edit_text.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (view.search_edit_text.toString() == ""){
                    //do something
                } else{
                    recyclerView?.visibility = View.VISIBLE
                    retrieveUsers()
                    searchUser(p0.toString().toLowerCase())
                }
            }

        })

        return view
    }

    private fun searchUser(input: String) {
        val query = FirebaseDatabase.getInstance()
            .reference
            .child("users")
            .orderByChild("fullName")
            .startAt(input)
            .endAt(input + "\uf8ff")

        query.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                mUser?.clear()

                for (snapshot in p0.children){
                    val user = snapshot.getValue(User::class.java)
                    if (user !=null){
                        mUser?.add(user)
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }
        })
    }

    private fun retrieveUsers() {
        val userRef = FirebaseDatabase.getInstance()
            .reference
            .child("users")
        userRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (view?.search_edit_text?.text.toString() == ""){
                    mUser?.clear()
                    for (snapshot in p0.children){
                        val user = snapshot.getValue(User::class.java)
                        if (user !=null){
                            mUser?.add(user)
                        }
                    }

                    userAdapter?.notifyDataSetChanged()
                }
            }
        })
    }

}
