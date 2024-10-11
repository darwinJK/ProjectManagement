package com.example.projecmanage.Activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.projecmanage.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressOnce = false
    private lateinit var mProgressDialog : Dialog
    var tv_progressbar : TextView? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_activiry)

        tv_progressbar = findViewById(R.id.progress_dialog_tv)
    }

    fun progressDialog(text:String){
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.progress_dialog)
        tv_progressbar?.text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.show()
    }

    fun dismissProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun getCurrentUserId():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToeExit(){
        if (doubleBackToExitPressOnce){
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressOnce = true
        Toast.makeText(this,resources.getString(R.string.click_back_application_exit),Toast.LENGTH_SHORT).show()
        Handler().postDelayed({
            doubleBackToExitPressOnce = false },1000)
    }

    fun showErrorSnackBar(message:String){
        val snackBar = Snackbar.make(findViewById(android.R.id.content),
            message,Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this,R.color.snackbar_error_color))
        snackBar.show()
    }

}