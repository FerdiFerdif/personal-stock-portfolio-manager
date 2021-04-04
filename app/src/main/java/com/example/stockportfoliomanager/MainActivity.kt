package com.example.stockportfoliomanager

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

//VOOR BEROEPSEXAMEN FERDI VAN DEN BROM | 2079865

class MainActivity : AppCompatActivity() {

    lateinit var handler: Handler
    lateinit var runnable: Runnable
    lateinit var auth: FirebaseAuth
    var db = FirebaseFirestore.getInstance()
    var pageCheck = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        Picasso.get().load(auth.currentUser.photoUrl).into(imageProfile)

        handler = Handler()
        runnable = Runnable {
            if (pageCheck) {
                Toast.makeText(
                        this@MainActivity,
                        "Logged out for inactivity",
                        Toast.LENGTH_SHORT
                ).show()
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
                pageCheck = false
            }
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

    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        restartTimer()
        startTimer()
    }

    override fun onRestart() {
        super.onRestart()
        pageCheck = true
        Picasso.get().load(auth.currentUser.photoUrl).into(imageProfile)
    }

    private fun restartTimer() {
        handler.removeCallbacks(runnable)
    }

    private fun startTimer() {
        handler.postDelayed(runnable, 1200000.toLong())
    }

}