package com.example.projecmanage.FireBase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.projecmanage.Activities.BaseActivity
import com.example.projecmanage.Activities.BoardActivity
import com.example.projecmanage.Activities.CardDetailsActivity
import com.example.projecmanage.Activities.MainActivity
import com.example.projecmanage.Activities.MembersActivity
import com.example.projecmanage.Activities.SignupActivity
import com.example.projecmanage.Activities.TaskListActivity
import com.example.projecmanage.Activities.profileInfoActivity
import com.example.projecmanage.Activities.signInActivity
import com.example.projecmanage.Models.Board
import com.example.projecmanage.Models.User
import com.example.projecmanage.Utils.Constants
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import kotlin.system.exitProcess

open class FireStoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    fun registerUser(activity : SignupActivity,userInfo : User){
          if(user!=null){
                mFireStore.collection(Constants.USERS).document(user.uid).set(
                    userInfo, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("test","fireStore.collection executed")
                        activity.userRegisteredSuccess()
                    }
                    .addOnFailureListener{
                            e->
                        activity.dismissProgressDialog()
                        BaseActivity().showErrorSnackBar("${e.message}")
                        Log.e("error","${e.message}")
                    }
          }
          else{
                Log.d("test","user is null $user")
          }
    }
    fun CreateBoard(activity:BoardActivity,board:Board){
        mFireStore.collection(Constants.BOARD)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(activity,"created successfully",Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener {
                exception->
                activity.dismissProgressDialog()
                Log.e("test","failed $exception")
            }
    }
    fun getBoardsList(activity: MainActivity){
        mFireStore.collection(Constants.BOARD)
            .whereArrayContains(Constants.ASSIGNED_TO, BaseActivity().getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document->
                Log.d("test" + activity.javaClass.simpleName,document.documents.toString())
                val boardList : ArrayList<Board> = ArrayList()
                for (i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardList.add(board)
                }
                activity.populateBoardList(boardList)
            }.addOnFailureListener {
                exception->
                activity.dismissProgressDialog()
                Log.e("test","creating failed ${activity.javaClass.simpleName}")
            }
    }

    fun updateUserProfileData(activity:Activity,userHashMap : HashMap<String,Any>){
        mFireStore.collection(Constants.USERS)
            .document(user!!.uid)
            .update(userHashMap)
            .addOnSuccessListener {
                Log.d("test","profile updated successfully")

                Toast.makeText(activity, "profile updated successfully",Toast.LENGTH_SHORT).show()
               when(activity){
                   is MainActivity -> {
                       activity.tokenUpdateSuccess()
                   }
                   is profileInfoActivity -> {
                       activity.dismissProgressDialog()
                       activity.simpleFunction()
                   }
               }
            }.addOnFailureListener {
                exception->
                when(activity){
                    is MainActivity -> {
                        activity.dismissProgressDialog()
                    }
                    is profileInfoActivity -> {
                        activity.dismissProgressDialog()
                    }
                }

                Log.e("test","Error updating user")
                Toast.makeText(activity, "profile update failed",Toast.LENGTH_SHORT).show()

            }
    }

    fun LoadUserData(activity: Activity,readBoardList:Boolean=false){
        mFireStore.collection(Constants.USERS).document(user!!.uid)
            .get().addOnSuccessListener {
                document->
                val loggedInUser = document.toObject(User::class.java)
                if(loggedInUser !=null){
                    when(activity){
                        is signInActivity ->
                            activity.signInSuccess(loggedInUser)
                        is MainActivity -> {
                            activity.updateNavigationDetails(loggedInUser,readBoardList)
                        }
                        is profileInfoActivity -> {
                            activity.setUserData(loggedInUser)
                        }
                    }

                }else{
                    Log.e("error","loggedUser is null")
                }

            }.addOnFailureListener {
                e->
                when(activity){
                    is signInActivity ->
                       activity.dismissProgressDialog()
                    is MainActivity -> {
                        activity.dismissProgressDialog()
                    }
                }
                Log.e("error","error occur : ${e.message}")
            }
    }

    fun getLoginUser():String{
        var currentUserId = ""
        if(user!=null)
            currentUserId = user.uid

        return currentUserId
    }

    fun getBoardsDetails(activity: TaskListActivity, boardDocumentId: String) {
        mFireStore.collection(Constants.BOARD)
            .document(boardDocumentId)
            .get()
            .addOnSuccessListener {
                    document->
                Log.d("test" + activity.javaClass.simpleName,document.toString())
                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id
                activity.boardDetails(board)

            }.addOnFailureListener {
                    exception->
                activity.dismissProgressDialog()
                Log.e("test","creating failed ${activity.javaClass.simpleName}")
            }
    }

    fun addUpdateTaskList(activity:Activity,board: Board){
        val taskListHashMap = HashMap<String,Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARD)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.d("test","TaskList updated successfully")
                if(activity is TaskListActivity)
                    activity.addUpdateSuccess()
                else if(activity is CardDetailsActivity)
                    activity.addUpdateSuccess()
            }
            .addOnFailureListener {
                exception->
                if(activity is TaskListActivity)
                    activity.dismissProgressDialog()
                else if(activity is CardDetailsActivity)
                    activity.dismissProgressDialog()
                Log.d("test","error occured in update task list $exception")

            }
    }

    fun getAssignedMembers(activity: Activity,assignedTo : ArrayList<String>){
        mFireStore.collection(Constants.USERS).whereIn(Constants.ID, assignedTo)
            .get().addOnSuccessListener {
                document->
                Log.e("test"+activity.javaClass.simpleName,document.documents.toString())

                val usersList : ArrayList<User> = ArrayList()
                for (i in document.documents){
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)
                }
                if(activity is MembersActivity){
                    activity.setupMembersList(usersList)
                }
                else if(activity is TaskListActivity){
                    activity.boardMembersDetails(usersList)
                }

            }
            .addOnFailureListener {
                exception->
                if(activity is MembersActivity){
                    activity.dismissProgressDialog()
                }
                else if(activity is TaskListActivity){
                    activity.dismissProgressDialog()
                }
                Log.e("test"+activity.javaClass.simpleName,"error while loading $exception")
            }
    }

    fun getMemberDetails(activity: MembersActivity,email:String){
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL,email)
            .get()
            .addOnSuccessListener {
                document->
                if(document.documents.size>0){
                    val user = document.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                }else{
                    activity.dismissProgressDialog()
                    activity.showErrorSnackBar("No such member found")
                }
            }.addOnFailureListener {
                exception->
                Log.e("test","error occured : $exception")
            }
    }

    fun assignedMemberToBoard(activity:MembersActivity,board:Board,user:User){
        val assignedToHashMap = HashMap<String,Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo

        mFireStore.collection(Constants.BOARD).document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignedSuccess(user)
            }.addOnFailureListener {
                    exception->
                activity.dismissProgressDialog()
                Log.e("test","error occured : $exception")
            }

    }
    /*private fun sendTokenToServer(user: User, fcmToken: String?) {
            val url = ""
        val client = OkHttpClient()
        val jsonBody = JSONObject().apply {
            put("userId",user.id)
            put(Constants.FCM_TOKEN,fcmToken)
        }
    }*/

}