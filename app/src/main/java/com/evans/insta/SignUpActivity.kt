package com.evans.insta

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*
import kotlin.collections.HashMap

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        sign_in_link.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        btn_register.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        val fullName = sign_up_full_name.text.toString().trim()
        val userName = sign_up_username.text.toString().trim()
        val email = sign_up_email.text.toString().trim()
        val password = sign_up_password.text.toString().trim()

        when {
            TextUtils.isEmpty(fullName) -> {
                sign_up_full_name.error = "Full Name is required"
                sign_up_full_name.requestFocus()
            }
            TextUtils.isEmpty(userName) -> {
                sign_up_username.error = "Username is required"
                sign_up_username.requestFocus()
            }
            TextUtils.isEmpty(email) -> {
                sign_up_email.error = "Email is required"
                sign_up_email.requestFocus()
            }
            TextUtils.isEmpty(password) -> {
                sign_up_password.error = "Password is required"
                sign_up_password.requestFocus()
            }
            else -> {
                val mDialog = ProgressDialog(this@SignUpActivity)
                mDialog.setTitle("Sign Up")
                mDialog.setMessage("Please wait...")
                mDialog.setCanceledOnTouchOutside(false)
                mDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            saveUserInfo(fullName, userName, email, mDialog)
                        } else {
                            val message = task.exception!!.toString()
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                            mAuth.signOut()
                            mDialog.dismiss()
                        }
                    }
            }
        }
    }

    private fun saveUserInfo(fullName: String, userName: String, email: String, mDialog: ProgressDialog) {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserId
        userMap["fullName"] = fullName
        userMap["userName"] = userName
        userMap["email"] = email
        userMap["bio"] = "Hey there, I am new here"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/insta-f9032.appspot.com/o/account.png?alt=media&token=42b49082-fc38-4bed-83dc-3fa6acb9741c"

        userRef.child(currentUserId)
            .setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    mDialog.dismiss()
                    Toast.makeText(this, "Success. Account created", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    val message = task.exception!!.toString()
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                    mDialog.dismiss()
                }
            }.addOnFailureListener { exception ->
                val error = exception.message
                Toast.makeText(this@SignUpActivity, "Error: $error", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                mDialog.dismiss()
            }
    }
}
