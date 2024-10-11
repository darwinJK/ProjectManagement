package com.example.projecmanage.Activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.projecmanage.FireBase.FireStoreClass
import com.example.projecmanage.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private var binding : ActivitySplashBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val typeface : Typeface = Typeface.createFromAsset(assets,"myfont.ttf")
        binding?.splashScreenText?.typeface = typeface

        Handler().postDelayed({
            var currentUserId = FireStoreClass().getLoginUser()
            if(currentUserId.isEmpty()){
                startActivity(Intent(this, IntroActivity::class.java))

            }else{
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()

        },2500)
    }
}