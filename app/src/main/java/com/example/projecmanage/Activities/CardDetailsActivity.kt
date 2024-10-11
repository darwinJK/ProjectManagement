package com.example.projecmanage.Activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.projecmanage.Adapters.CardMemberListItemAdapter
import com.example.projecmanage.Adapters.LabelColorListAdapter
import com.example.projecmanage.Dialogs.LabelColorLIstDialog
import com.example.projecmanage.Dialogs.SelectedMemberDialog
import com.example.projecmanage.FireBase.FireStoreClass
import com.example.projecmanage.Models.Board
import com.example.projecmanage.Models.Card
import com.example.projecmanage.Models.SelectedMembers
import com.example.projecmanage.Models.Tasks
import com.example.projecmanage.Models.User
import com.example.projecmanage.R
import com.example.projecmanage.Utils.Constants
import com.example.projecmanage.databinding.ActivityCardDetailsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CardDetailsActivity : BaseActivity() {

    private lateinit var mBoardDetails : Board
    private var mTaskListPosition : Int= -1
    private var mCardPosition : Int = -1
    private var mSelectedColor : String= ""
    private lateinit var mMembersDetailsList : ArrayList<User>

    private var mSelectedDueDateMilliSec : Long = 0

    private var binding : ActivityCardDetailsBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        getIntentData()
        setSupportActionBar()
        binding?.etNameCardDetails?.setText(
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if(mSelectedColor.isNotEmpty()){
            setColor()
        }

        binding?.updateCardDetails?.setOnClickListener {
            if(binding?.etNameCardDetails?.text.toString().isNotEmpty()){
                updateCardDetails()
            }else{
                Toast.makeText(this@CardDetailsActivity,"please enter a card name",Toast.LENGTH_SHORT).show()
            }
        }
        binding?.tvSelectLabelColor?.setOnClickListener {
            labelColorsListDialog()
        }

        binding?.tvSelectMembers?.setOnClickListener {
            selectedMemberListDialog()
        }
        binding?.tvSelectDueDate?.setOnClickListener {
            showDatePicker()
        }
        mSelectedDueDateMilliSec = mBoardDetails.taskList[mTaskListPosition]
            .cards[mCardPosition].dueDate
        if(mSelectedDueDateMilliSec> 0){
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH)
            val selectedDate = simpleDateFormat.format(Date( mSelectedDueDateMilliSec))
            binding?.tvSelectDueDate?.text = selectedDate
        }

        setupSelectedMembersList()


    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun colorsList():ArrayList<String>{
        val colorsList : ArrayList<String> = ArrayList()
        colorsList.add("#e51010")
        colorsList.add("#ff6600")
        colorsList.add("#fc4589")
        colorsList.add("#4adeaf")
        colorsList.add("#00b2ff")
        colorsList.add("#420e87")
        return colorsList
    }

    private fun setColor(){
        binding?.tvSelectLabelColor?.text = ""
        binding?.tvSelectLabelColor?.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete_card_menu -> {
               showAlertDialog(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setSupportActionBar(){
        setSupportActionBar(binding?.toolbarCardActivity)
        val toolbar = supportActionBar
        if(toolbar!=null){
            toolbar.setDisplayHomeAsUpEnabled(true)
            toolbar.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name
            toolbar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
        }
        binding?.toolbarCardActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    private fun getIntentData(){
        if(intent.hasExtra(Constants.Board_Detail))
            mBoardDetails = intent.getParcelableExtra(Constants.Board_Detail)!!

        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION))
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)

        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION))
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST))
            mMembersDetailsList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
    }

    fun addUpdateSuccess() {
        dismissProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }
    private fun updateCardDetails(){
        val card = Card(
            binding?.etNameCardDetails?.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor,
            mSelectedDueDateMilliSec
            )

        val taskList:ArrayList<Tasks> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card

        progressDialog("please wait")
        FireStoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)
    }

    private fun deleteCard(){
        val cardList : ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards

        cardList.removeAt(mCardPosition)
        val taskList:ArrayList<Tasks> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)
        taskList[mTaskListPosition].cards = cardList

        progressDialog("please wait")
        FireStoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)
    }

    private fun showAlertDialog(cardName:String) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Alert")
        alertDialog.setMessage("Are you sure you want to delete $cardName")
        alertDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
        }
        alertDialog.setPositiveButton("yes"){
                dialog,which ->
            deleteCard()
            dialog.dismiss()

        }
        alertDialog.setNegativeButton("No"){
                dialog,_->
            dialog.dismiss()
        }
        val dialog = alertDialog.create()
        dialog.show()
    }

    private fun labelColorsListDialog(){
        val colorsList : ArrayList<String> = colorsList()
        val listDialog = object : LabelColorLIstDialog(
            this@CardDetailsActivity,
            colorsList,
            "Select label color",
            mSelectedColor)
        {
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }

        }
        listDialog.show()
    }

    private fun selectedMemberListDialog() {
        var cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition]
            .cards[mCardPosition].assignedTo
        if(cardAssignedMembersList.size>0){
            for(i in mMembersDetailsList.indices){
                for (j in cardAssignedMembersList){
                    if(mMembersDetailsList[i].id==j){
                        mMembersDetailsList[i].selected = true
                    }
                }
            }
        }else{
            for(i in mMembersDetailsList.indices) {
                mMembersDetailsList[i].selected = false
            }
        }
        val listDialog = object :SelectedMemberDialog(this@CardDetailsActivity,
            mMembersDetailsList,"Select the member"){
            override fun onItemSelected(user: User, action: String) {

                if(action==Constants.SELECT) {
                    if (!mBoardDetails.taskList[mTaskListPosition]
                            .cards[mCardPosition].assignedTo.contains(user.id)
                    ) {

                        mBoardDetails.taskList[mTaskListPosition]
                            .cards[mCardPosition].assignedTo.add(user.id!!)
                    }
                }else{
                    mBoardDetails.taskList[mTaskListPosition]
                        .cards[mCardPosition].assignedTo.remove(user.id)

                    for(i in  mMembersDetailsList.indices){
                        if (mMembersDetailsList[i].id==user.id){
                            mMembersDetailsList[i].selected=false
                        }
                    }
                }
                setupSelectedMembersList()
            }
        }
        listDialog.show()
    }
    private fun setupSelectedMembersList(){
        val carAssignedMemberList  = mBoardDetails.taskList[mTaskListPosition]
            .cards[mCardPosition].assignedTo
        val selectedMembersList : ArrayList<SelectedMembers> = ArrayList()

        for(i in mMembersDetailsList.indices){
            for (j in carAssignedMemberList){
                if(mMembersDetailsList[i].id==j){
                    val selectedMember = SelectedMembers(
                        mMembersDetailsList[i].id!!,
                        mMembersDetailsList[i].image!!)
                    selectedMembersList.add(selectedMember)
                }
            }
        }
        if (selectedMembersList.size>0){
            selectedMembersList.add(SelectedMembers("",""))
            binding?.tvSelectMembers?.visibility = View.GONE
            binding?.rvSelectedMembersList?.visibility = View.VISIBLE

            binding?.rvSelectedMembersList?.layoutManager = GridLayoutManager(
                this,6
            )
            val adapter = CardMemberListItemAdapter(this,selectedMembersList,true)
            binding?.rvSelectedMembersList?.adapter = adapter
            adapter.setOnClickListener(object : CardMemberListItemAdapter.OnClickListener{
                override fun onClick() {
                    selectedMemberListDialog()
                }

            })
        }else{
            binding?.tvSelectMembers?.visibility = View.VISIBLE
            binding?.rvSelectedMembersList?.visibility = View.GONE
        }
    }

    private fun showDatePicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener {
                                               view, year, month, dayOfMonth ->
                val sDayOfMonth = if (dayOfMonth<10) "0$dayOfMonth" else "$dayOfMonth"
                val sMonthOfYear = if((month+1)<10) "0${month+1}" else "${month+1}"

                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                binding?.tvSelectDueDate?.text = selectedDate

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val theDate = sdf.parse(selectedDate)
                mSelectedDueDateMilliSec = theDate!!.time
            },
            year,
            month,
            day)
        dpd.show()

    }
}