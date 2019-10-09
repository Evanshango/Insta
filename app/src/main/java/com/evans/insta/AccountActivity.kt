package com.evans.insta

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.evans.insta.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_account.*
import java.util.*
import kotlin.collections.HashMap

class AccountActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        userInfo()

        btn_account_log_out.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
            startActivity(Intent(this@AccountActivity, SignInActivity::class.java))
        }

        btn_save_profile_info.setOnClickListener {
            if (checker == "clicked") {

            } else {
                updateUserInfo()
            }
        }
    }

    private fun updateUserInfo() {
        val name = account_name.text.toString()
        val username = account_username.text.toString()
        val bio = account_bio.text.toString()

        if (name.isEmpty() || username.isEmpty() || bio.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
        } else {
            val userRef = FirebaseDatabase.getInstance().reference.child("users")
            val userMap = HashMap<String, Any>()
            userMap["fullName"] = name.toLowerCase(Locale.getDefault())
            userMap["userName"] = username.toLowerCase(Locale.getDefault())
            userMap["bio"] = bio

            userRef.child(firebaseUser.uid).updateChildren(userMap)
            Toast.makeText(this, "Account info updated successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun userInfo() {
        val userRef = FirebaseDatabase
            .getInstance()
            .reference
            .child("users")
            .child(firebaseUser.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user = p0.getValue<User>(User::class.java)
                    Glide.with(this@AccountActivity)
                        .load(user!!.getImage())
                        .into(edit_profile_photo as CircleImageView)

                    account_username?.setText(user.getUserName())
                    account_name?.setText(user.getFullName())
                    account_bio?.setText(user.getBio())
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@AccountActivity, p0.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
