package com.example.projecmanage.Activities

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.projecmanage.Adapters.MembersListAdapter
import com.example.projecmanage.FireBase.FireStoreClass
import com.example.projecmanage.Models.Board
import com.example.projecmanage.Models.User
import com.example.projecmanage.R
import com.example.projecmanage.Utils.Constants
import com.example.projecmanage.databinding.ActivityMembersBinding
import com.google.api.Http
import java.net.HttpURLConnection

class MembersActivity : BaseActivity() {

    private var binding : ActivityMembersBinding? = null

    private lateinit var mBoardDetails : Board
    private lateinit var mAssignedMembersList : ArrayList<User>
    private var anyChangeMade : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if(intent.hasExtra(Constants.Board_Detail)){
            mBoardDetails = intent.getParcelableExtra(Constants.Board_Detail)!!
        }
        setSupportActionBar()
        progressDialog("please wait")
        FireStoreClass().getAssignedMembers(this,mBoardDetails.assignedTo)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_add_member->{
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setSupportActionBar(){
        setSupportActionBar(binding?.toolbarMemberActivity)
        val toolbar = supportActionBar
        if(toolbar!=null){
            toolbar.setDisplayHomeAsUpEnabled(true)
            toolbar.title = "Members"
            toolbar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
        }
        binding?.toolbarMemberActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if(anyChangeMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    fun setupMembersList(list:ArrayList<User>){
        mAssignedMembersList = list
        dismissProgressDialog()
        binding?.rvMembersList?.layoutManager = LinearLayoutManager(this)
        binding?.rvMembersList?.setHasFixedSize(true)
        val adapter= MembersListAdapter(this,list)
        binding?.rvMembersList?.adapter = adapter
    }

    private fun dialogSearchMember(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.member_custom_dialog)
        dialog.findViewById<TextView>(R.id.tv_add_member).setOnClickListener {
            val email = dialog.findViewById<EditText>(R.id.et_member_email).text.toString()
            if(email.isNotEmpty()){
                progressDialog(("please wait"))
                FireStoreClass().getMemberDetails(this,email)
                dialog.dismiss()
            }else{
                Toast.makeText(this@MembersActivity,"enter email id",Toast.LENGTH_SHORT).show()

            }
        }
        dialog.findViewById<TextView>(R.id.tv_cancel_member).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun memberDetails(user: User){
        mBoardDetails.assignedTo.add(user.id!!)
        FireStoreClass().assignedMemberToBoard(this,mBoardDetails,user)
    }

    fun memberAssignedSuccess(user:User){
        dismissProgressDialog()
        mAssignedMembersList.add(user)
        anyChangeMade = true
        setupMembersList(mAssignedMembersList)
    }

    /*  private inner class sendNotificationUserAsyncTask : AsyncTask<Any,Void,String>(){
          override fun onPreExecute() {
              super.onPreExecute()
              progressDialog("please wait")
          }
          override fun doInBackground(vararg params: Any?): String {
              var result : String
              var connection : HttpURLConnection? = null
              try{
                  val url
              }

              return result
          }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            dismissProgressDialog()
        }

    }*/
}