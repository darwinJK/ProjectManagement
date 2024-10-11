package com.example.projecmanage.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.projecmanage.FireBase.FireStoreClass
import com.example.projecmanage.Models.User
import com.example.projecmanage.R
import com.example.projecmanage.databinding.ActivitySignInBinding
import com.example.projecmanage.fcm.MyFirebaseMessagingService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class signInActivity : BaseActivity() {

    private var binding : ActivitySignInBinding? = null
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setSupportActionBar()

        binding?.btnLoginBtn?.setOnClickListener {
            checksForEmptyValue()

        }
    }

    private fun checksForEmptyValue() {
        val email = binding?.emailSignInEt?.text.toString().trim{ it <= ' '}
        val password = binding?.passwprdSignInEt?.text.toString()
        when{
            TextUtils.isEmpty(email) -> showErrorSnackBar("please enter email to Login")
            TextUtils.isEmpty(password) -> showErrorSnackBar("please enter password to Login")
            else->{
                loginUser(email,password)
            }
        }
    }


    private fun loginUser(email:String,password:String) {
        progressDialog(resources.getString(R.string.please_wait))
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
            task->
                if(task.isSuccessful) {
                    FireStoreClass().LoadUserData(this)
                }else{
                    showErrorSnackBar("${task.exception?.message}")
                }
        }
    }

    private fun setSupportActionBar(){
        setSupportActionBar(binding?.toolbarSignInActivity)

        val actionbar = supportActionBar
        if(actionbar!=null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
            actionbar.title = "SIGN IN"
        }
        binding?.toolbarSignInActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }
    fun signInSuccess(user: User){
        dismissProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}