package com.evans.insta.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.evans.insta.R
import com.evans.insta.models.User
import com.evans.insta.utils.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    private var mContext: Context,
    private var mUser: List<User>,
    private var isFragment: Boolean = false
) : RecyclerView.Adapter<UserAdapter.UserHolder>() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false)
        return UserHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {

        val user: User = mUser[position]
        holder.bind(user)

        checkFollowingStatus(user.getUid(), holder.follow)

        holder.follow.setOnClickListener {
            if (holder.follow.text.toString() == "Follow") {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUid())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUid())
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }
                                }
                            }
                        }
                }
            } else {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUid())
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUid())
                                        .child("Followers").child(it1.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }
                                }
                            }
                        }
                }
            }
        }
    }

    private fun checkFollowingStatus(uid: String, follow: Button) {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }

        followingRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(uid).exists()){
                    follow.text = mContext.getString(R.string.following)
                } else{
                    follow.text = mContext.getString(R.string.follow)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    class UserHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var userNameTxt: TextView = itemView.findViewById(R.id.user_name_search)
        private var fullNameTxt: TextView = itemView.findViewById(R.id.user_full_name_search)
        private var profileImage: CircleImageView =
            itemView.findViewById(R.id.user_profile_image_search)
        var follow: Button = itemView.findViewById(R.id.btn_follow_search)

        fun bind(user: User) {
            userNameTxt.text = user.getUserName()
            fullNameTxt.text = user.getFullName()

            val profile: String = user.getImage()
            GlideApp.with(itemView.context)
                .load(profile)
                .into(profileImage)
        }
    }
}