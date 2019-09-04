package com.evans.insta

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        sign_up_link.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        btn_log_in.setOnClickListener {
            logInUser()
        }
    }

    private fun logInUser() {
        val email = sign_in_email.text.toString().trim()
        val password = sign_in_password.text.toString().trim()

        when {
            TextUtils.isEmpty(email) -> {
                sign_in_email.error = "Email can't be empty"
                sign_in_email.requestFocus()
            }
            TextUtils.isEmpty(password) -> {
                sign_in_password.error = "Password can't be empty"
                sign_in_password.requestFocus()
            }
            else -> {
                val mDialog = ProgressDialog(this@SignInActivity)
                mDialog.setTitle("Sign In")
                mDialog.setMessage("Please wait...")
                mDialog.setCanceledOnTouchOutside(false)
                mDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            mDialog.dismiss()
                            finish()
                            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                            Toast.makeText(this@SignInActivity, "Welcome", Toast.LENGTH_SHORT).show()
                        } else{
                            mDialog.dismiss()
                            val message = task.exception
                            Toast.makeText(this@SignInActivity, "Error: $message", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { exception ->
                        mDialog.dismiss()
                        val error = exception.message
                        Toast.makeText(this@SignInActivity, "Error: $error", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

}
