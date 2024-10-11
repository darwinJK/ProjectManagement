package com.example.projecmanage.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.projecmanage.Adapters.BoardItemsAdapter
import com.example.projecmanage.FireBase.FireStoreClass
import com.example.projecmanage.Models.Board
import com.example.projecmanage.Models.Notification
import com.example.projecmanage.Models.NotificationData
import com.example.projecmanage.Models.User
import com.example.projecmanage.R
import com.example.projecmanage.Utils.Constants
//import com.example.projecmanage.apis.NotificationApi
import com.example.projecmanage.databinding.ActivityMainBinding
import com.example.projecmanage.databinding.NavHeaderMainBinding
import com.example.projecmanage.fcm.MyFirebaseMessagingService
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.messaging.FirebaseMessaging
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileInputStream

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var binding : ActivityMainBinding? = null
    private lateinit var mUserName : String

    private lateinit var mSharedPreferences: SharedPreferences

    companion object{
        const val CREATE_BOARD_REQUEST_CODE : Int =12


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setUpActionBar()
        binding?.navView?.setNavigationItemSelectedListener(this)

        mSharedPreferences = this.getSharedPreferences(Constants.PROJEMANAGE_PREFERENCES
            ,Context.MODE_PRIVATE)
        val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATE,false)
        if(tokenUpdated){
            progressDialog("please wait")
            FireStoreClass().LoadUserData(this,true)
        }else{
            FirebaseMessaging.getInstance().token.addOnSuccessListener(this@MainActivity){
                token->
                updateFcmToken(token)
            }
        }

        FireStoreClass().LoadUserData(this,true)

        MyFirebaseMessagingService(this)

      //  permissionForNotification()

        binding?.includeAppMain?.fabCreateBoard?.setOnClickListener {
            val intent = Intent(this,BoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE){
            FireStoreClass().getBoardsList(this)
        }
    }

    fun populateBoardList(boardList : ArrayList<Board>){
        dismissProgressDialog()
        val rvBinding = binding?.includeAppMain?.appMainInclude
        if(boardList.size>0){
            rvBinding?.rvBoardList?.visibility = View.VISIBLE
            rvBinding?.noBoardRecord?.visibility = View.GONE

            rvBinding?.rvBoardList?.layoutManager = LinearLayoutManager(this)
            rvBinding?.rvBoardList?.setHasFixedSize(true)
            val adapter = BoardItemsAdapter(boardList,this)
            rvBinding?.rvBoardList?.adapter = adapter
            adapter.setOnClickListener(object:BoardItemsAdapter.OnclickListener{
                override fun onCLick(position: Int, model: Board) {
                   val intent = Intent(this@MainActivity,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,model.documentId)
                    startActivity(intent)
                }

            })
        }else{
            rvBinding?.rvBoardList?.visibility = View.GONE
            rvBinding?.noBoardRecord?.visibility = View.VISIBLE
        }
    }

    private fun setUpActionBar(){
        val toolbar = findViewById<Toolbar>(R.id.toolbar_main_Activity)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.baseline_menu_24)
        toolbar.setNavigationOnClickListener{
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            doubleBackToeExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
       // val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        when(item.itemId){
            R.id.nav_my_profile->{
                Toast.makeText(this,"profile clicked",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,profileInfoActivity::class.java))

            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                mSharedPreferences.edit().clear().apply()

                val intent = Intent(this,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

        }
        //drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateNavigationDetails(user:User,readBoardList:Boolean){
        val header = findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
        val navBinding = NavHeaderMainBinding.bind(header)

        dismissProgressDialog()
        val fcmToken = user.fcmToken
        Log.d("fcmToken","$fcmToken")
        mUserName = user.name.toString()
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.profile)
            .into(navBinding.navUserImage)

        navBinding.tvUsername.text = user.name
        if(readBoardList){
            progressDialog("please wait")
            FireStoreClass().getBoardsList(this)
        }
    }

    fun tokenUpdateSuccess() {
        dismissProgressDialog()
        val editor:SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATE,true)
        editor.apply()
        progressDialog("please wait")
        FireStoreClass().LoadUserData(this,true)
    }


    private fun updateFcmToken(token : String){
        val userHashMap = HashMap<String,Any>()
        userHashMap[Constants.FCM_TOKEN] = token
        progressDialog("please wait")
        FireStoreClass().updateUserProfileData(this,userHashMap)
    }
    private fun permissionForNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            Dexter.withContext(this)
                .withPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                .withListener(object : PermissionListener{
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        TODO("Not yet implemented")
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        TODO("Not yet implemented")
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        Token: PermissionToken?
                    ) {
                       Token?.continuePermissionRequest()
                    }

                }).check()
        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        FirebaseMessaging.getInstance().subscribeToTopic("Test")

        sendNotification()
    }
    private fun sendNotification(){
        val notification = Notification(
            message = NotificationData(
                topic ="test",
                hashMapOf("title" to "You have loggedIn",
                "body" to "You have successfully signedIn to ProjecManage.")
            )
        )
        /*NotificationApi.sendNotification().notification(notification).enqueue(
            object : Callback<Notification>{
                override fun onResponse(p0: Call<Notification>, p1: Response<Notification>) {
                    Toast.makeText(this@MainActivity,"notification successfull",Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(p0: Call<Notification>, t: Throwable) {
                    Toast.makeText(this@MainActivity,"notification failed ${t.message}",Toast.LENGTH_SHORT).show()

                }

            }

        )

         */
    }

}