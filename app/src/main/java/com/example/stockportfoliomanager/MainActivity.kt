package com.example.stockportfoliomanager

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

//VOOR BEROEPSEXAMEN FERDI VAN DEN BROM | 2079865

class MainActivity : AppCompatActivity() {

    lateinit var handler: Handler
    lateinit var runnable: Runnable
    lateinit var auth: FirebaseAuth
    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        val docRef = db.collection("users").document(auth.currentUser.uid)
        val source = Source.CACHE
        docRef.get(source).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Document found in the offline cache
                val document = task.result
                if (document != null) {
                    savedString.text = document.getString("dataString")
                }
            }
        }

        Picasso.get().load(auth.currentUser.photoUrl).into(imageProfile)

        handler = Handler()
        runnable = Runnable {

            Toast.makeText(
                    this@MainActivity,
                    "Logged out for inactivity",
                    Toast.LENGTH_SHORT
            ).show()
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }
        startTimer()

        val userId = intent.getStringExtra("user_id")
        val emailId = intent.getStringExtra("email_id")

        userIdUID.text = "UserId : $userId"
        userIdEmail.text = "Email : $emailId"

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }

        portfolioButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, PortfolioActivity::class.java))
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }

        testButton.setOnClickListener {
            when {
                TextUtils.isEmpty(log_string.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                            this@MainActivity,
                            "Please enter a value",
                            Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val user: MutableMap<String, Any> = HashMap()
                    user["dataString"] = log_string.text.toString()

                    db.collection("users").document("$userId")
                            .set(user, SetOptions.merge())
                            .addOnSuccessListener {
                                Toast.makeText(this@MainActivity, "Successfully updated data.", Toast.LENGTH_LONG).show()
                                savedString.text = log_string.text.toString()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@MainActivity, "Failed to update data.", Toast.LENGTH_LONG).show()
                            }
                }
            }
        }

    }
    override fun onUserInteraction() {
        super.onUserInteraction()
        restartTimer()
        startTimer()
    }

    override fun onRestart() {
        super.onRestart()
        Picasso.get().load(auth.currentUser.photoUrl).into(imageProfile)
    }

    private fun restartTimer() {
        handler.removeCallbacks(runnable)
    }
    private fun startTimer() {
        handler.postDelayed(runnable, 1200000.toLong())
    }

}