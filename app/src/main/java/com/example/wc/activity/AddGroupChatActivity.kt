package com.example.wc.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.example.wc.R
import com.example.wc.adapter.AddGroupChatRecyclerViewAdapter
import com.example.wc.util.Chat
import com.example.wc.util.SPHelp
import kotlinx.android.synthetic.main.activity_add_group_chat.*


class AddGroupChatActivity : AppCompatActivity(), View.OnClickListener{
    private var chatList:ArrayList<Chat>?=null
    private var adapter:AddGroupChatRecyclerViewAdapter?=null
    private var selectResultList:ArrayList<Chat>?= ArrayList() //选择结果

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_group_chat)
        window.statusBarColor = Color.parseColor("#ededed")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager//列表布局
        adapter=AddGroupChatRecyclerViewAdapter(this,object :AddGroupChatRecyclerViewAdapter.OnSelectResultListener{
            override fun onSelect(list: ArrayList<Chat>) {
                selectResultList=list
                if(list.size>0){
                    tvSubmit.text="确定(${list.size})"
                    tvSubmit.isClickable=true
                    tvSubmit.setBackgroundResource(R.drawable.r2_green_normal)
                }else {
                    tvSubmit.text="确定"
                    tvSubmit.isClickable=false
                    tvSubmit.setBackgroundResource(R.drawable.r2_gray_normal)
                }
            }
        })
        recyclerView.adapter=adapter
        chatList= SPHelp.getInstance(this).getChatList()
        adapter?.setData(chatList)
        ivBack.setOnClickListener(this)
        tvSubmit.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack ->finish()
            R.id.tvSubmit ->{//新建一个群聊
                if(selectResultList?.size==1){
                    val bundle = Bundle()
                    bundle.putSerializable("chat",selectResultList!![0])
                    startActivityForResult(Intent(this, ChatActivity::class.java).putExtras(bundle),1)
                    return
                }
                val chat=Chat()
                chat.position=0
                chat.userId=System.currentTimeMillis().toString()
                val arrayList=ArrayList<Int>()
                for(chat1 in selectResultList!!){
                    chat1.userHeadLsit?.get(0)?.let { arrayList.add(it) }
                }
                chat.userHeadLsit=arrayList
                chat.userName="群聊(${arrayList.size+1})"
                val bundle = Bundle()
                bundle.putSerializable("chat",chat)
                bundle.putString("tag","add")//设置标识与聊天列表跳过去的区分
                startActivityForResult(Intent(this, ChatActivity::class.java).putExtras(bundle),1)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1 && resultCode==1){//聊天页面返回
            setResult(2)
            finish()
        }
    }

}
