package com.example.wc.util

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


import java.util.ArrayList



class SPHelp private constructor(context: Context?) {
    var sp: SharedPreferences?=null

    init {
        sp =context?.getSharedPreferences("message_sp", Context.MODE_PRIVATE)
    }

    companion object {
        fun getInstance(context:Context?):SPHelp{
            return SPHelp(context)
        }
    }

    fun saveMessage(message:Message?,tag:String?){
        sp?.edit()?.putString(tag, Gson().toJson(message))?.apply()
    }

    fun getMessage(tag:String?):Message{
        val message=sp?.getString(tag,"")
        return if(TextUtils.isEmpty(message)){
            Message()
        }else Gson().fromJson(message,Message::class.java)
    }

    fun getChatList():ArrayList<Chat>{
        val tmepstr=sp?.getString("chat_list","")
        return if(TextUtils.isEmpty(tmepstr)){
            ArrayList()
        }else Gson().fromJson(tmepstr, object : TypeToken<ArrayList<Chat>>() {}.type)
    }

    fun setChatList(list:ArrayList<Chat>){
        sp?.edit()?.putString("chat_list", Gson().toJson(list))?.apply()
    }






}
