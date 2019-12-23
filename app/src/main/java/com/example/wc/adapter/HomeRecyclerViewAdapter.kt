package com.example.wc.adapter


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.wc.R
import com.example.wc.activity.ChatActivity
import com.example.wc.util.Chat
import com.example.wc.util.RoundTransformation
import com.example.wc.util.constant
import kotlinx.android.synthetic.main.item_home.view.*


class HomeRecyclerViewAdapter(var context:Context?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var chatList:ArrayList<Chat>?=null
    private val ivIds5to9= intArrayOf(R.id.iv9,R.id.iv8,R.id.iv7,R.id.iv6,R.id.iv5,R.id.iv4,R.id.iv3,R.id.iv2,R.id.iv1)
    private val ivIds2to4= intArrayOf(R.id.iv14,R.id.iv13,R.id.iv12,R.id.iv11)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    fun setData(chatList:ArrayList<Chat>?){
        this.chatList=chatList
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_home, p0, false))
    }


    override fun getItemCount(): Int {
        return if(chatList!=null) chatList!!.size else 0
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(chatList!![position].userHeadLsit!!.size>1){//群聊
            holder.itemView.ivHead.visibility=View.GONE
            if(chatList!![position].userHeadLsit!!.size<4){
                holder.itemView.clHead5to9.visibility=View.GONE
                holder.itemView.clHead2to4.visibility=View.VISIBLE
                for (i in 0..chatList!![position].userHeadLsit!!.size){
                    holder.itemView.findViewById<ImageView>(ivIds2to4[i]).visibility=View.VISIBLE
                    if(i<chatList!![position].userHeadLsit!!.size){
                        Glide.with(context).load(chatList!![position].userHeadLsit!![i]).into(holder.itemView.findViewById(ivIds2to4[i]))
                    }else{
                        Glide.with(context).load(constant.userHead).into(holder.itemView.findViewById(ivIds2to4[i]))
                    }
                }
            }else {
                holder.itemView.clHead5to9.visibility = View.VISIBLE
                holder.itemView.clHead2to4.visibility=View.GONE
                for (i in 0..chatList!![position].userHeadLsit!!.size) {
                    holder.itemView.findViewById<ImageView>(ivIds5to9[i]).visibility = View.VISIBLE
                    if (i < chatList!![position].userHeadLsit!!.size) {
                        Glide.with(context).load(chatList!![position].userHeadLsit!![i])
                            .into(holder.itemView.findViewById(ivIds5to9[i]))
                    } else {
                        Glide.with(context).load(constant.userHead).into(holder.itemView.findViewById(ivIds5to9[i]))
                    }
                }
            }
         //   Glide.with(context).load(R.mipmap.qunliao).into(holder.itemView.ivHead)
            holder.itemView.tvName.text = chatList!![position].userName
        }else {//单人
            holder.itemView.ivHead.visibility=View.VISIBLE
            holder.itemView.clHead5to9.visibility=View.GONE
            holder.itemView.clHead2to4.visibility=View.GONE
            Glide.with(context).load(chatList!![position].userHeadLsit!![0])
                .transform(RoundTransformation(context!! ,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5f,context!!.resources.displayMetrics).toInt()))
                .into(holder.itemView.ivHead)
            holder.itemView.tvName.text = chatList!![position].userName
        }
        holder.itemView.tvLastMessage.text = chatList!![position].lastMessage
        if (chatList!![position].lastChatTime != null) {
            holder.itemView.tvTime.text = constant.getTime(chatList!![position].lastChatTime!!.toLong())
        }else holder.itemView.tvTime.text=""
        holder.itemView.cl.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("chat",chatList!![position])
            val activity = context as Activity
            activity.startActivityForResult(Intent(context, ChatActivity::class.java).putExtras(bundle),1)
        }
    }

}