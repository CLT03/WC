package com.example.wc

import android.app.Application
import com.example.wc.util.Chat
import com.example.wc.util.SPHelp

class WCAPP : Application(){
    private val listUserName = arrayOf("罗密欧和猪过夜","按键伤人","煮人为快乐之本","温柔一刀","无情的泡面","掐死你的温柔","黑洲非人","全幼儿园最可爱","你咋不上天","最佳隐身奖")//默认用户名
    private val userHead = arrayOf(//默认用户头像
        R.mipmap.head0, R.mipmap.head1, R.mipmap.head2,
        R.mipmap.head3, R.mipmap.head4, R.mipmap.head5, R.mipmap.head6,
        R.mipmap.head7, R.mipmap.head8, R.mipmap.head9)
    override fun onCreate() {
        super.onCreate()
        val list=SPHelp.getInstance(this).getChatList()
        if(list.size==0){//首次打开app默认设置10个聊天加一个群聊
            for(i in 0..9){
                val chat=Chat()
                chat.userName=listUserName[i]
                val chatHeadList=ArrayList<Int>()
                chatHeadList.add(userHead[i])
                chat.userHeadLsit=chatHeadList
                chat.userId=i.toString()
                chat.position=i
                list.add(chat)
            }
            val chat=Chat()
            chat.position=10
            chat.userId=System.currentTimeMillis().toString()
            val arrayList=ArrayList<Int>()
            arrayList.add(userHead[1])
            arrayList.add(userHead[2])
            arrayList.add(userHead[3])
            chat.userHeadLsit=arrayList
            chat.userName="群聊(${arrayList.size+1})"
            list.add(chat)
            SPHelp.getInstance(this).setChatList(list)
        }
    }
}