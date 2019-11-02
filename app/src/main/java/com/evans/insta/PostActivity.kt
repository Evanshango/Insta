package com.evans.insta

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_post.*
import kotlin.collections.HashMap

class PostActivity : AppCompatActivity() {

    private var mUrl = ""
    private var imageUri: Uri? = null
    private var postPicRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        postPicRef = FirebaseStorage.getInstance().reference.child("post_pics")

        btn_save_post.setOnClickListener {
            uploadImage()
        }

        CropImage.activity()
            .setAspectRatio(2, 1)
            .start(this@PostActivity)
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Add Post")
        progressDialog.setMessage("Adding a new post. Please wait...")
        progressDialog.show()

        when {
            imageUri == null -> Toast.makeText(
                this,
                "Please select an image",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(description_post.text.toString()) -> Toast.makeText(
                this,
                "Description needed",
                Toast.LENGTH_SHORT
            ).show()
            else -> {
                val fileRef = postPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")
                val uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                        progressDialog.dismiss()
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        mUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("posts")
                        val postId = ref.push().key
                        val postMap = HashMap<String, Any>()
                        postMap["postId"] = postId!!
                        postMap["description"] = description_post.text.toString()
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postImage"] = mUrl

                        ref.child(postId).updateChildren(postMap)

                        Toast.makeText(this, "Post uploaded successfully", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this@PostActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    } else {
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
            && data != null
        ) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            image_post.setImageURI(imageUri)
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }
}
