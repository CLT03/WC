package com.example.wc.util

import java.util.ArrayList

/*
聊天信息
 */
class Message {
      var chatMessageList: ArrayList<String> = ArrayList()  //消息内容
      var chatTagList: ArrayList<Int> = ArrayList()   //消息类型
      var chatHeadList: ArrayList<Int> = ArrayList()  //用户头像
}