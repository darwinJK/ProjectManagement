package com.example.projecmanage.Activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Toast

import com.bumptech.glide.Glide
import com.example.projecmanage.FireBase.FireStoreClass
import com.example.projecmanage.Models.User
import com.example.projecmanage.R
import com.example.projecmanage.Utils.Constants
import com.example.projecmanage.Utils.Constants.CAMERA
import com.example.projecmanage.Utils.Constants.GALLERY
import com.example.projecmanage.Utils.Constants.IMAGE
import com.example.projecmanage.Utils.Constants.MOBILE
import com.example.projecmanage.Utils.Constants.NAME
import com.example.projecmanage.databinding.ActivityProfileInfoBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


class profileInfoActivity : BaseActivity() {

    var binding : ActivityProfileInfoBinding? = null

   private var mSelectedImageFileUri : Uri? = null
    private var mProfileImageUri : String =""
    private lateinit var mUserDetails : User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileInfoBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setSupportActionBar()

        FireStoreClass().LoadUserData(this)

        binding?.profileUserImage?.setOnClickListener{
            showDialogForTakeAction()
        }

        binding?.updateBtnProfile?.setOnClickListener {
            if(mSelectedImageFileUri!=null){
                uploadUserImage()
            }else{
                progressDialog("please wait")
                updateUserProfileData()
            }
        }
    }

    private fun showDialogForTakeAction() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Upload image")
        val cameraOrMedia = arrayOf("Select photo from gallery",
           // "Take a photo"
        )
        dialog.setItems(cameraOrMedia){
                dialog,which ->
            when(which){
                0-> selectFromGallery()
             //   1-> takeAPicture()
            }
        }
        dialog.show()
    }

    private fun takeAPicture() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)
            .withListener(object : MultiplePermissionsListener{
                override fun onPermissionsChecked(result: MultiplePermissionsReport?) {
                    if(result!!.areAllPermissionsGranted()){
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent,CAMERA)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    Constants.showDialogAlert(this@profileInfoActivity,
                        "You have to grant the required permissions For updating the profile picture")
                }


            }).check()
    }

    private fun selectFromGallery() {

        Dexter.withActivity(this)
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener{
                override fun onPermissionsChecked(result: MultiplePermissionsReport?) {
                    if(result!!.areAllPermissionsGranted()){
                        val gallery =Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(gallery,GALLERY)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    Constants.showDialogAlert(this@profileInfoActivity,
                        "You have to grant the required permissions For updating the profile picture")
                }


            }).check()
    }


    private fun setSupportActionBar(){
        setSupportActionBar(binding?.toolbarProfileActivity)
        val toolbar = supportActionBar
        if(toolbar!=null){
            toolbar.setDisplayHomeAsUpEnabled(true)
            toolbar.title = resources.getString(R.string.my_profile_title)
            toolbar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
        }
        binding?.toolbarProfileActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            Log.d("test","resultcode == result.ok")
            if(requestCode== GALLERY){
                Log.d("test","requestcode == GALLERY")
                if(data!=null){
                    Log.d("test","data is not null")
                    val contentUri = data.data
                    mSelectedImageFileUri = contentUri
                    try{
                        Glide
                            .with(this)
                            .load(mSelectedImageFileUri)
                            .centerCrop()
                            .placeholder(R.drawable.profile)
                            .into(binding?.profileUserImage!!)

                    }catch (e:Exception){
                        Log.e("test error","error : ${e.message}")
                        e.printStackTrace()
                    }
                }
            }else if(requestCode == CAMERA){
                Log.d("test","requestcode==camera")
                val bitmap = data!!.extras!!.get("data") as Bitmap
                Glide
                    .with(this)
                    .load(bitmap)
                    .centerCrop()
                    .placeholder(R.drawable.profile)
                    .into(binding?.profileUserImage!!)
            }
        }
    }

    private fun updateUserProfileData(){
        val userHashMap = HashMap<String,Any>()

        if(mProfileImageUri.isNotEmpty() && mProfileImageUri != mUserDetails.image){
            userHashMap[IMAGE] = mProfileImageUri
        }
        if(binding?.nameProfileEt?.text.toString() != mUserDetails.name){
            userHashMap[NAME] = binding?.nameProfileEt?.text.toString()

        }
        if(binding?.mobileProfileEt?.text.toString() != mUserDetails.mobile.toString()){
            userHashMap[MOBILE] = (binding?.mobileProfileEt?.text.toString()).toLong()

        }
            FireStoreClass().updateUserProfileData(this,userHashMap)
    }

    fun setUserData(user: User) {
        mUserDetails= user
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.profile)
            .into(binding?.profileUserImage!!)

        binding?.nameProfileEt?.setText(user.name)
        binding?.emailProfileEt?.setText(user.email)
        if(user.mobile!=0L){
            binding?.mobileProfileEt?.setText(user.mobile.toString())
        }
    }

    private fun uploadUserImage(){
        progressDialog("please wait")
        if(mSelectedImageFileUri!=null){
            val sRef : StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "."
                        + Constants.getFileExtension(this,mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapShot->
                Log.e("Firebase Image url", taskSnapShot.metadata!!
                    .reference!!.downloadUrl.toString())

                taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    mProfileImageUri = uri.toString()
                    Log.e("downloadable image Uri", uri.toString())

                    updateUserProfileData()

                }
            }.addOnFailureListener {
                exception->
                Toast.makeText(this@profileInfoActivity,"$exception",Toast.LENGTH_SHORT).show()
                dismissProgressDialog()
            }

        }
    }




    fun simpleFunction() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

}