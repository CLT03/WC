package com.example.wc.util

import java.io.Serializable
import java.util.*

/*
聊天记录列表信息
 */
 class Chat : Serializable {

     var userName:String?=null
     var userId:String?=null
     var lastMessage:String?=null
     var lastChatTime:String?=null
     var userHeadLsit:ArrayList<Int>?=null //群聊用户头像 但不包括自己
     var position:Int=0  //所在聊天信息列表的位置

}