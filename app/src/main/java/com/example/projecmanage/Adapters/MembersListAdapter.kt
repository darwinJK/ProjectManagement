package com.example.projecmanage.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projecmanage.Models.Board
import com.example.projecmanage.Models.User
import com.example.projecmanage.R
import com.example.projecmanage.Utils.Constants
import de.hdodenhof.circleimageview.CircleImageView

class MembersListAdapter(private var context : Context,
    private var list : ArrayList<User>) :RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_member,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is ViewHolder){
            val binding = holder.itemView.findViewById<CircleImageView>(R.id.iv_member_image)
           Glide
               .with(context)
               .load(model.image)
               .centerCrop()
               .placeholder(R.drawable.profile)
               .into(binding)
            holder.itemView.findViewById<TextView>(R.id.tv_member_name).text = model.name
            holder.itemView.findViewById<TextView>(R.id.member_email).text = model.email

            if(model.selected){
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_member).visibility = View.VISIBLE

            }else{
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_member).visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                if(onClickListener!=null){
                    if(model.selected){
                        onClickListener!!.onClick(position,model,Constants.UN_SELECT)
                    }else{
                        onClickListener!!.onClick(position,model,Constants.SELECT)
                    }
                }
            }
        }
    }
    fun setOnclickListener(onClickListener : OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int,user: User,action:String)
    }
}
private class ViewHolder(view: View):RecyclerView.ViewHolder(view)