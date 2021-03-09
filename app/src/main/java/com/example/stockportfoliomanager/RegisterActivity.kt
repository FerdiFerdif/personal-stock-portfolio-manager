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

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        loginButton.setOnClickListener {

            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }


        registerAccountButton.setOnClickListener {
            when {
                TextUtils.isEmpty(register_email.text.toString().trim {it <= ' '}) -> {
                    Toast.makeText(
                            this@RegisterActivity,
                            "Please enter email",
                            Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(register_password.text.toString().trim {it <= ' '}) -> {
                    Toast.makeText(
                            this@RegisterActivity,
                            "Please enter password",
                            Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {

                        val email: String = register_email.text.toString().trim { it <= ' '}
                        val password: String = register_password.text.toString().trim { it <= ' '}

                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                        val firebaseUser: FirebaseUser = task.result!!.user!!

                                        Toast.makeText(
                                                this@RegisterActivity,
                                                "Successfully registered",
                                                Toast.LENGTH_SHORT
                                        ).show()

                                        val intent =
                                                Intent(this@RegisterActivity, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        intent.putExtra("user_id", firebaseUser.uid)
                                        intent.putExtra("email_id", email)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        // Toast error message
                                        Toast.makeText(
                                                this@RegisterActivity,
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