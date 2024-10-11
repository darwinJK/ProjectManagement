package com.example.projecmanage.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.projecmanage.FireBase.FireStoreClass
import com.example.projecmanage.Models.User
import com.example.projecmanage.R
import com.example.projecmanage.Utils.Constants
import com.example.projecmanage.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SignupActivity :BaseActivity() {

    private var binding : ActivitySignupBinding? = null

    private val mFireStore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setSupportActionBar()

        binding?.btnSignUpBtn?.setOnClickListener {
            registerUser()
        }



    }

    private fun setSupportActionBar(){
        setSupportActionBar(binding?.toolbarSignupActivity)

        val actionbar = supportActionBar
        if(actionbar!=null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
            actionbar.title = "SIGN UP"
        }
        binding?.toolbarSignupActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    private fun registerUser(){
        val name : String = binding?.nameSignupEt?.text.toString().trim{it <= ' '}
        val email : String = binding?.emailSignupEt?.text.toString().trim{it <= ' '}
        val password : String = binding?.passwprdSignupEt?.text.toString()

        if(validateForm(name, email, password)){

            progressDialog(resources.getString(R.string.please_wait))

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid,name,registeredEmail)
                        Log.d("test","working before registerUser")
                        FireStoreClass().registerUser(this,user)
                        dismissProgressDialog()
                    } else {
                        dismissProgressDialog()
                        Toast.makeText(
                            this@SignupActivity, "${task.exception}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

        }
    }

    private fun validateForm(name:String, email:String, password:String):Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("please enter a name")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("please enter a email")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("please enter a password")
                false
            }else->{
                true
            }
        }
    }

   fun userRegisteredSuccess(){
       Log.d("test","working inside unregistered user")
        Toast.makeText(
            this@SignupActivity, "you have successfully Registered",
            Toast.LENGTH_SHORT
        ).show()
        FirebaseAuth.getInstance().signOut()
        finish()
    }
}