package com.example.projecmanage.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projecmanage.Adapters.LabelColorListAdapter
import com.example.projecmanage.Adapters.MembersListAdapter
import com.example.projecmanage.R
import com.example.projecmanage.Models.User

abstract class SelectedMemberDialog(context:Context,
                           private var list : ArrayList<User>,
                           private var title : String
) : Dialog(context){


    private var adapter : MembersListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list,null)
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerview(view)
    }
    private fun setUpRecyclerview(view: View) {
        view.findViewById<TextView>(R.id.tvTitle).text = title
        if(list.size>0){
            view.findViewById<RecyclerView>(R.id.rvList_color).layoutManager =
                LinearLayoutManager(context)
            adapter = MembersListAdapter(context, list)
            view.findViewById<RecyclerView>(R.id.rvList_color).adapter = adapter

            adapter!!.setOnclickListener(object : MembersListAdapter.OnClickListener {
                override fun onClick(position: Int, user: User, action: String) {
                    Toast.makeText(context,"clicked",Toast.LENGTH_SHORT).show()
                    dismiss()
                    onItemSelected(user,action)
                }

            })
        }

    }
    protected abstract fun onItemSelected(user:User,action:String)
}