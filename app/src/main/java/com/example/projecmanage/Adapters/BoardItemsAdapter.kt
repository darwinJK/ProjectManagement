package com.example.projecmanage.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projecmanage.Models.Board
import com.example.projecmanage.R

open class BoardItemsAdapter(private var board : ArrayList<Board>,
    private var context:Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var onClikListener : OnclickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate
            (R.layout.item_board, parent,false))
    }

    override fun getItemCount(): Int {
        return board.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = board[position]
        if(holder is MyViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.baseline_image_24)
                .into(holder.itemView.findViewById(R.id.board_image_rv))

            val name = holder.itemView.findViewById<TextView>(R.id.board_name_tv)
            val creator = holder.itemView.findViewById<TextView>(R.id.created_by_tv)
            name.text = model.name
            creator.text = "Created by: ${model.createdBy}"

            holder.itemView.setOnClickListener {
                if(onClikListener!=null){
                    onClikListener!!.onCLick(position,model)
                }
            }
        }
    }
    interface OnclickListener{
        fun onCLick(position: Int,model:Board)
    }
    fun setOnClickListener(onClickListener:OnclickListener){
            this.onClikListener=onClickListener
    }
    private class MyViewHolder(view: View):RecyclerView.ViewHolder(view)
}
