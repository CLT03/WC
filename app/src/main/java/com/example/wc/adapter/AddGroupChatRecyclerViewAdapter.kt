package com.example.wc.adapter


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.wc.R
import com.example.wc.activity.ChatActivity
import com.example.wc.activity.ImgSelectActivity
import com.example.wc.util.Chat
import com.example.wc.util.RoundTransformation
import com.example.wc.util.SPHelp
import com.example.wc.util.constant
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.item_add_group_chat.view.*
import kotlinx.android.synthetic.main.item_home.view.*
import kotlinx.android.synthetic.main.item_home.view.ivHead
import kotlinx.android.synthetic.main.item_home.view.tvName


class AddGroupChatRecyclerViewAdapter(var context:Context?,var listener:OnSelectResultListener?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var chatList:ArrayList<Chat>?=ArrayList()
    private var selectResultList:ArrayList<Chat>?= ArrayList()//选择结果

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    fun setData(chatList:ArrayList<Chat>?){
        for(chat in chatList!!){ //去掉群聊部分和自己
            if(chat.userHeadLsit?.size!!<2 && chat.userId!="0")
                this.chatList?.add(chat)
        }
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_add_group_chat, p0, false))
    }


    override fun getItemCount(): Int {
        return if(chatList!=null) chatList!!.size else 0
    }





    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Glide.with(context).load(chatList!![position].userHeadLsit!![0])
            .transform(RoundTransformation(context!! , TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5f,context!!.resources.displayMetrics).toInt()))
            .into(holder.itemView.ivHead)
        holder.itemView.tvName.text = chatList!![position].userName
        holder.itemView.ivSelect.setOnClickListener {
            if(selectResultList!!.contains(chatList!![position])){
                holder.itemView.ivSelect.setImageResource(R.mipmap.select_no)
                selectResultList!!.remove(chatList!![position])
                listener?.onSelect(selectResultList!!)
            }else {
                holder.itemView.ivSelect.setImageResource(R.mipmap.select_yes)
                selectResultList!!.add(chatList!![position])
                listener?.onSelect(selectResultList!!)
            }
        }
    }

    interface OnSelectResultListener{
        fun onSelect(list:ArrayList<Chat>)
    }

}