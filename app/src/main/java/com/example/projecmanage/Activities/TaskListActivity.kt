package com.example.projecmanage.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projecmanage.Adapters.TaskListItemAdapter
import com.example.projecmanage.FireBase.FireStoreClass
import com.example.projecmanage.Models.Board
import com.example.projecmanage.Models.Card
import com.example.projecmanage.Models.Tasks
import com.example.projecmanage.Models.User
import com.example.projecmanage.R
import com.example.projecmanage.Utils.Constants
import com.example.projecmanage.databinding.ActivityTaskListBinding

class TaskListActivity : BaseActivity() {
    var binding : ActivityTaskListBinding? = null
    private lateinit var mBoardDetails : Board
    private lateinit var mBoardDocumentId : String
    lateinit var mAssignedMemberDetailList : ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
            //progressDialog("please wait")
            FireStoreClass().getBoardsDetails(this,mBoardDocumentId)
        }

    }
    fun cardDetails(taskLIstPosition : Int,cardPosition: Int){
        val intent = Intent(this,CardDetailsActivity::class.java)
        intent.putExtra(Constants.Board_Detail,mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,taskLIstPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION,cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST,mAssignedMemberDetailList)
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }

    override fun onResume() {
        progressDialog("please wait")
        FireStoreClass().getBoardsDetails(this,mBoardDocumentId)
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode==Activity.RESULT_OK
            && requestCode == MEMBER_REQUEST_CODE
            || requestCode== CARD_DETAILS_REQUEST_CODE){
          //  progressDialog("please wait")
            FireStoreClass().getBoardsDetails(this,mBoardDocumentId)
        }else{
            Log.e("test","cancel")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members->{
                val intent = Intent(this,MembersActivity::class.java)
                intent.putExtra(Constants.Board_Detail,mBoardDetails)
                startActivityForResult(intent,MEMBER_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun boardDetails(board: Board){
        mBoardDetails = board
        dismissProgressDialog()
        setUpActionBar()



        progressDialog("please wait")
        FireStoreClass().getAssignedMembers(this,mBoardDetails.assignedTo)
    }

    fun addUpdateSuccess(){
        dismissProgressDialog()  //load activity successful
        progressDialog("please wait")
        FireStoreClass().getBoardsDetails(this,mBoardDetails.documentId)
    }

    fun updateTaskList(position:Int,listName:String,model: Tasks){
        val task = Tasks(listName,model.createdBy)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        progressDialog("please wait")
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun createTaskList(taskLIstName:String){
        //TODO some dbt in getCurrent user below he called it froom fireClass
        val task = Tasks(taskLIstName,getCurrentUserId())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        progressDialog("please wait")
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding?.toolbarTaskActivity)

        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
            actionbar.title = mBoardDetails.name
        }
        binding?.toolbarTaskActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun deleteTaskList(position:Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        progressDialog("please wait")
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun addCardToTaskList(position: Int,cardName : String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        val cardAssignedUsersList  : ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(getCurrentUserId())

        val card = Card(cardName,getCurrentUserId(),cardAssignedUsersList)

        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)

        val task = Tasks(mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList)

        mBoardDetails.taskList[position] = task

        progressDialog("please wait")
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun boardMembersDetails(list:ArrayList<User>){
        mAssignedMemberDetailList= list

        dismissProgressDialog()

        val addTaskList = Tasks(resources.getString(R.string.add_list))
        mBoardDetails.taskList.add(addTaskList)
        binding?.rvTaskLiist?.layoutManager = LinearLayoutManager(
            this,LinearLayoutManager.HORIZONTAL,false)
        binding?.rvTaskLiist?.setHasFixedSize(true)
        val adapter = TaskListItemAdapter(this,mBoardDetails.taskList)
        binding?.rvTaskLiist?.adapter = adapter
    }

    fun UpdatecardsInTaskList(taskLIstPosition: Int,cards : ArrayList<Card>){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        mBoardDetails.taskList[taskLIstPosition].cards = cards
        progressDialog("please wait")
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }

     companion object{
         const val MEMBER_REQUEST_CODE : Int = 13
         const val CARD_DETAILS_REQUEST_CODE : Int =14
     }

}