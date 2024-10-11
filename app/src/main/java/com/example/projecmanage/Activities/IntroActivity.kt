package com.example.projecmanage.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.projecmanage.R
import com.example.projecmanage.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {
    private var binding : ActivityIntroBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding?.btnSignUpIntro?.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
        }
        binding?.btnSignInIntro?.setOnClickListener {
            startActivity(Intent(this,signInActivity::class.java))
        }
    }
}