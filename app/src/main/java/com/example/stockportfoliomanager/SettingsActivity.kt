package com.example.stockportfoliomanager

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.IOException

//VOOR BEROEPSEXAMEN FERDI VAN DEN BROM | 2079865

class SettingsActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    lateinit var auth: FirebaseAuth
    lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().reference

        selectImageButton.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(galleryIntent, 102)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
            }
        }

        uploadImageButton.setOnClickListener {
            if(selectedImageUri != null) {
                val imageExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(selectedImageUri!!))
                val serverRef = storageRef.child("${auth.currentUser.uid}")

                serverRef.putFile(selectedImageUri!!)
                        .addOnSuccessListener { taskSnapshot ->
                            taskSnapshot.metadata!!.reference!!.downloadUrl
                                    .addOnSuccessListener { url ->

                                        val profileNewUpdate = UserProfileChangeRequest.Builder()
                                                .setPhotoUri(url)
                                                .build()

                                        auth.currentUser.updateProfile(profileNewUpdate)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {

                                                        Toast.makeText(
                                                                this@SettingsActivity,
                                                                "Successfully changed profile picture to: $url.",
                                                                Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        Toast.makeText(
                                                                this@SettingsActivity,
                                                                task.exception!!.message.toString(),
                                                                Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                    }
                                    .addOnFailureListener{ exception ->
                                        Toast.makeText(
                                                this@SettingsActivity,
                                                exception.message,
                                                Toast.LENGTH_LONG
                                        ).show()
                                        Log.e(javaClass.simpleName, exception.message, exception)
                                    }
                        }
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101){
            val galleryIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(galleryIntent, 102)
        } else {
            Toast.makeText(
                    this@SettingsActivity,
                    "Permission denied.",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 102) {
                if (data != null) {
                    try {
                        selectedImageUri = data.data!!
                        imageProfileVi.setImageURI(selectedImageUri)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                                this@SettingsActivity,
                                "Failed to select image.",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}