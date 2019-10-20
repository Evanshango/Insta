package com.evans.insta

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.evans.insta.models.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_account.*
import java.util.*
import kotlin.collections.HashMap

class AccountActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var mUrl = ""
    private var imageUri: Uri? = null
    private var profilePicStorage: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        profilePicStorage = FirebaseStorage.getInstance().reference.child("profile_pics")
        userInfo()

        btn_account_log_out.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
            startActivity(Intent(this@AccountActivity, SignInActivity::class.java))
        }

        btn_save_profile_info.setOnClickListener {
            if (checker == "clicked") {
                uploadImageAndUpdateInfo()
            } else {
                updateUserInfo()
            }
        }

        txt_edit_photo.setOnClickListener {
            checker = "clicked"
            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this)
        }
    }

    private fun uploadImageAndUpdateInfo() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Account Settings")
        progressDialog.setMessage("Updating account info. Please wait...")
        progressDialog.show()

        val name = account_name.text.toString()
        val username = account_username.text.toString()
        val bio = account_bio.text.toString()

        if (name.isEmpty() || username.isEmpty() || bio.isEmpty()) {
            progressDialog.dismiss()
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
        } else if (imageUri == null){
            progressDialog.dismiss()
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
        } else{
            val profileRef = profilePicStorage!!.child(firebaseUser.uid + ".jpg")
            val uploadTask: StorageTask<*>
            uploadTask = profileRef.putFile(imageUri!!)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {task ->
                if (!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                    progressDialog.dismiss()
                }
                return@Continuation profileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val downloadUrl = task.result
                    mUrl = downloadUrl.toString()

                    val ref = FirebaseDatabase.getInstance().reference.child("users")
                    val userMap = HashMap<String, Any>()
                    userMap["fullName"] = name.toLowerCase(Locale.getDefault())
                    userMap["userName"] = username.toLowerCase(Locale.getDefault())
                    userMap["bio"] = bio
                    userMap["image"] = mUrl

                    ref.child(firebaseUser.uid).updateChildren(userMap)

                    Toast.makeText(this, "Account info updated successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    progressDialog.dismiss()
                } else{
                    progressDialog.dismiss()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
            && data != null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            edit_profile_photo.setImageURI(imageUri)
        } else{
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
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
