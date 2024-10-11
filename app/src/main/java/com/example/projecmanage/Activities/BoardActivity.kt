package com.example.projecmanage.Activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.projecmanage.FireBase.FireStoreClass
import com.example.projecmanage.Models.Board
import com.example.projecmanage.R
import com.example.projecmanage.Utils.Constants
import com.example.projecmanage.databinding.ActivityBoardBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class BoardActivity : BaseActivity() {

    private var binding : ActivityBoardBinding? = null

    private var mSelectedImageFileUri : Uri? = null
    private var mBoardImageUri : String = ""
    lateinit var muserName : String
    private lateinit var mBoard : Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar()
        if(intent.hasExtra(Constants.NAME)){
            muserName=intent.getStringExtra(Constants.NAME).toString()
        }

        binding?.boardIvImage?.setOnClickListener {
            showDialogForTakeAction()
        }
        binding?.btnCreateBtn?.setOnClickListener {
            if(mSelectedImageFileUri!=null){
                uploadBoardImage()
            }else{
                progressDialog("please wait")
                createBoardData()
            }
        }
    }

    private fun setSupportActionBar(){
        setSupportActionBar(binding?.toolbarBoardActivity)
        val toolbar = supportActionBar
        if(toolbar!=null){
            toolbar.setDisplayHomeAsUpEnabled(true)
            toolbar.title = resources.getString(R.string.create_board)
            toolbar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
        }
        binding?.toolbarBoardActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    private fun showDialogForTakeAction() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Upload image")
        val cameraOrMedia = arrayOf("Select photo from gallery",
            //"Take a photo"
            )
        dialog.setItems(cameraOrMedia){
                dialog,which ->
            when(which){
                0-> selectFromGallery()
               // 1-> takeAPicture()
            }
        }
        dialog.show()
    }


   private  fun takeAPicture() {
        Dexter.withActivity(this)
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener{
                override fun onPermissionsChecked(result: MultiplePermissionsReport?) {
                    if(result!!.areAllPermissionsGranted()){
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent,Constants.CAMERA)

                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    Constants.showDialogAlert(this@BoardActivity,"It looks like you have turned of the permission require for this feature.It can be enabled under application settings")
                }

            }).check()
    }

   private fun selectFromGallery() {
       Log.d("test","inside gallery")
       Dexter.withActivity(this)
           .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)
           .withListener(object : MultiplePermissionsListener{
               override fun onPermissionsChecked(result: MultiplePermissionsReport?) {
                   if(result!!.areAllPermissionsGranted()){
                       Log.d("test","inside permission granted")
                        val intent = Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                       startActivityForResult(intent,Constants.GALLERY)
                   }else{
                       Log.d("test","permission denied")
                   }
               }
               override fun onPermissionRationaleShouldBeShown(
                   p0: MutableList<PermissionRequest>?,
                   p1: PermissionToken?
               ) {
                   Constants.showDialogAlert(this@BoardActivity,
                       "You have to grant the required permissions For updating the profile picture")
               }

           }).check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.GALLERY){
                if(data!=null){
                   mSelectedImageFileUri = data.data
                        try{
                            Glide
                                .with(this)
                                .load(mSelectedImageFileUri)
                                .centerCrop()
                                .placeholder(R.drawable.profile)
                                .into(binding?.boardIvImage!!)

                        }catch (e:Exception){
                            Log.e("test error","error : ${e.message}")
                            e.printStackTrace()
                        }
                }
            }else if(requestCode== Constants.CAMERA){
                val thumbnail : Bitmap =data!!.extras!!.get("data") as Bitmap
                binding?.boardIvImage?.setImageBitmap(thumbnail)

            }
        }
    }

    private fun uploadBoardImage(){
        progressDialog("please wait")

            val sRef:StorageReference = FirebaseStorage.getInstance().reference.child(
                "BOARD_IMAGE" + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(this,mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapShot->
                Log.d("FirebaseImageUrl",taskSnapShot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    mBoardImageUri = uri.toString()
                    Log.d("test", mBoardImageUri)
                    createBoardData()
                }
            }.addOnFailureListener {
                exception->
                Toast.makeText(this@BoardActivity,"$exception", Toast.LENGTH_SHORT).show()
                dismissProgressDialog()
            }
    }

    private fun createBoardData() {
        val assignedUserSArrayList : ArrayList<String> = ArrayList()
        assignedUserSArrayList.add(getCurrentUserId())

        val board = Board(
            name = binding?.boardNameEt?.text.toString(),
            image = mBoardImageUri,
            createdBy = muserName,
            assignedTo = assignedUserSArrayList)

        FireStoreClass().CreateBoard(this,board)
    }


    fun boardCreatedSuccessfully(){
        dismissProgressDialog()
        setResult(RESULT_OK)
       // startActivity(Intent(this,MainActivity::class.java))
        Log.d("test","board created successfuly")
        finish()
    }
}