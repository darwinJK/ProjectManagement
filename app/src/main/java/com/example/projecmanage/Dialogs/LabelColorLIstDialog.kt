package com.example.projecmanage.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projecmanage.Adapters.LabelColorListAdapter
import com.example.projecmanage.R

abstract class LabelColorLIstDialog(
    context: Context,
    private var list : ArrayList<String>,
    private val title : String ="",
    private val mSelectedColor : String = "")
    : Dialog(context){

        private var adapter : LabelColorListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list,null)
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerview(view)
    }

    private fun setUpRecyclerview(view:View){
        view.findViewById<TextView>(R.id.tvTitle).text = title
        view.findViewById<RecyclerView>(R.id.rvList_color).layoutManager = LinearLayoutManager(context)
        adapter = LabelColorListAdapter(context,list,mSelectedColor)
        view.findViewById<RecyclerView>(R.id.rvList_color).adapter = adapter

        adapter!!.onItemClickListener = object : LabelColorListAdapter.OnItemClickListener{
            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)
            }

        }
    }
    protected abstract fun onItemSelected(color:String)
}