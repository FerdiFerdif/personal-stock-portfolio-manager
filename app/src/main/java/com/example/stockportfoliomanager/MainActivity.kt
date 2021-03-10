package com.example.stockportfoliomanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

//VOOR BEROEPSEXAMEN FERDI VAN DEN BROM | 2079865

class MainActivity : AppCompatActivity() {

    lateinit var handler: Handler
    lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "KotlinApp"
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

    }
    override fun onUserInteraction() {
        super.onUserInteraction()
        restartTimer()
        startTimer()
    }
    private fun restartTimer() {
        handler.removeCallbacks(runnable)
    }
    private fun startTimer() {
        handler.postDelayed(runnable, 1200000.toLong())
    }
}