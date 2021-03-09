package com.example.stockportfoliomanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

//VOOR BEROEPSEXAMEN FERDI VAN DEN BROM | 2079865

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        registerButton.setOnClickListener {

            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        loginAccountButton.setOnClickListener {
            when {
                TextUtils.isEmpty(login_email.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                            this@LoginActivity,
                            "Please enter email",
                            Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(login_password.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                            this@LoginActivity,
                            "Please enter password",
                            Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {

                    val email: String = login_email.text.toString().trim { it <= ' ' }
                    val password: String = login_password.text.toString().trim { it <= ' ' }

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                    val firebaseUser: FirebaseUser = task.result!!.user!!

                                    Toast.makeText(
                                            this@LoginActivity,
                                            "Successfully logged in",
                                            Toast.LENGTH_SHORT
                                    ).show()

                                    val intent =
                                            Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.putExtra("user_id", firebaseUser.uid)
                                    intent.putExtra("email_id", email)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    // Toast error message
                                    Toast.makeText(
                                            this@LoginActivity,
                                            task.exception!!.message.toString(),
                                            Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                }
            }
        }

    }
}