package com.example.projecmanage.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projecmanage.Activities.TaskListActivity
import com.example.projecmanage.Models.Board
import com.example.projecmanage.Models.Card
import com.example.projecmanage.Models.SelectedMembers
import com.example.projecmanage.R

class CardListItemsAdapter(
    private var context : Context,
    private var list  : ArrayList<Card>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder){

            if (model.labelColor.isNotEmpty()){

                holder.itemView.findViewById<View>(R.id.view_label_color).visibility = View.VISIBLE

                holder.itemView.findViewById<View>(R.id.view_label_color).
                setBackgroundColor(Color.parseColor(model.labelColor))
            }else{
                holder.itemView.findViewById<View>(R.id.view_label_color).visibility = View.GONE
            }


            holder.itemView.findViewById<TextView>(R.id.tv_card_name).text = model.name

            if((context as TaskListActivity).mAssignedMemberDetailList.size>0){
                val selectedMembersList : ArrayList<SelectedMembers> = ArrayList()

                for(i in (context as TaskListActivity).mAssignedMemberDetailList.indices){
                    for(j in model.assignedTo){
                        if((context as TaskListActivity).mAssignedMemberDetailList[i].id ==j){
                            val selectedMembers = SelectedMembers(
                                (context as TaskListActivity).mAssignedMemberDetailList[i].id!!,
                                (context as TaskListActivity).mAssignedMemberDetailList[i].image!!
                            )
                            selectedMembersList.add(selectedMembers)
                        }
                    }
                }
                if(selectedMembersList.size>0){
                    if(selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy){
                        holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list)
                            .visibility = View.GONE
                    }else{
                        holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list)
                            .visibility = View.VISIBLE

                        holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list)
                            .layoutManager = GridLayoutManager(context,4)

                        val adapter = CardMemberListItemAdapter(context,selectedMembersList,false)

                        holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list)
                            .adapter = adapter

                        adapter.setOnClickListener(object : CardMemberListItemAdapter.OnClickListener{
                            override fun onClick() {
                                if(onClickListener!=null){
                                    onClickListener!!.onClick(position)
                                }
                            }

                        })

                    }
                }else{
                    holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list)
                        .visibility = View.GONE
                }
            }

            holder.itemView.setOnClickListener {
                onClickListener?.onClick(position)
            }
        }
    }
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
    interface OnClickListener{
        fun onClick(cardPosition: Int)
    }
    private class MyViewHolder(view: View):RecyclerView.ViewHolder(view)
}




