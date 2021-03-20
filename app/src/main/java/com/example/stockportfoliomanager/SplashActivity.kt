package com.example.stockportfoliomanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash.*

//VOOR BEROEPSEXAMEN FERDI VAN DEN BROM | 2079865

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        logo.alpha = 0f
        logo.animate().setDuration(2500).alpha(1f).withEndAction {
            val e = Intent(this, LoginActivity::class.java)
            startActivity(e)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }
    }
}