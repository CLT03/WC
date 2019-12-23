package com.example.wc.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_chat.*
import android.util.TypedValue
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Glide
import com.example.wc.adapter.ChatRecyclerViewAdapter
import com.example.wc.R
import com.example.wc.util.*


class ChatActivity : AppCompatActivity(), View.OnClickListener,
    ChatRecyclerViewAdapter.ImgScaleListener {


    private  var mIfKeyboardShow: Boolean = false
    private var adapter: ChatRecyclerViewAdapter? = null
    private var message:Message?=null
    private var chatUserId:String?=null
    private var chat:Chat?=null
    private var lastTime: Long = 0L //最后聊天时间
    private var hasChange:Boolean=false //是否发了消息
    private var chatHeadList:ArrayList<Int>?=null //头像
    private var newGroupChat:Boolean=false //是否是新建的群聊
    private var mxPrecent: Float? = null //点击图片中心位置在屏幕宽度的比例
    private var myPrecent: Float? = null //点击图片中心位置在屏幕高度的比例

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        window.statusBarColor = Color.parseColor("#ededed")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        edtMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (edtMessage.text.isNotEmpty()) {
                    tvSend.visibility = View.VISIBLE
                } else {
                    tvSend.visibility = View.GONE
                }
            }
        })
        //初始化信息
        val bundle=intent.extras
        chat=bundle?.getSerializable("chat") as Chat
        if(!TextUtils.isEmpty(bundle.getString("tag",""))) {//来自新建群聊
            newGroupChat = true
            //更新外面列表
            val chatList=SPHelp.getInstance(this).getChatList()
            for (i in 0 until chatList.size) {
                chatList[i].position = chatList[i].position + 1
            }
            chatList.add(0, chat!!)
            SPHelp.getInstance(this).setChatList(chatList)
        }
        tvTitle.text=chat?.userName
        chatUserId=chat?.userId
        chatHeadList=chat?.userHeadLsit
        if(chat?.lastChatTime!=null) {
            lastTime = chat?.lastChatTime!!.toLong()
        }

        val layoutManger = LinearLayoutManager(this)
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        //layoutManger.stackFromEnd=true //从底部开始显示
        layoutManger.reverseLayout = true //列表翻转
        recyclerView.layoutManager = layoutManger
        recyclerView.addItemDecoration(SpaceItemDecoration())
        adapter = ChatRecyclerViewAdapter(this, this)
        message=SPHelp.getInstance(this).getMessage(chat?.userId)
        adapter?.addMessage(message!!.chatMessageList,message!!.chatTagList,message!!.chatHeadList)
        recyclerView.adapter = adapter
        layoutManger.scrollToPositionWithOffset(0, Integer.MIN_VALUE)//滑动到底部
        viewHideKeyW.setOnClickListener(this)
        ivScale.setOnClickListener(this)
        ivMore.setOnClickListener(this)
        tvSend.setOnClickListener(this)
        ivBack.setOnClickListener(this)

        SoftKeyBoardListener.setListener(
            this,
            object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
                override fun keyBoardShow(height: Int) {
                 //   Log.e("ououou", "keyBoardShow" + height)
                    mIfKeyboardShow = true
                    viewHideKeyW.visibility = View.VISIBLE
                    layoutManger.scrollToPositionWithOffset(adapter?.itemCount!! - 1, Integer.MAX_VALUE)//滑动到底部
                }

                override fun keyBoardHide(height: Int) {
                 //   Log.e("ououou", "keyBoardHide" + height)
                    mIfKeyboardShow = false
                    viewHideKeyW.visibility = View.GONE
                }
            })

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.viewHideKeyW -> //空白处的view
                if (mIfKeyboardShow) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
                }
            R.id.ivScale -> if (ivScale.visibility == View.VISIBLE) {//放大的图片
                hideScaleImg()
            }
            R.id.ivMore -> {
                startActivityForResult(Intent(this@ChatActivity, ImgSelectActivity::class.java),1)
                overridePendingTransition(R.anim.anim_in, 0)
            }
            R.id.tvSend -> {
                val chatMessageList = ArrayList<String>()
                val chatTagList = ArrayList<Int>()
                val chatHeadList = ArrayList<Int>()
                for(i in 0 until this.chatHeadList!!.size){ //先加除了自己的
                    chatMessageList.add(edtMessage.text.toString())
                    chatTagList.add(ChatRecyclerViewAdapter.LEFT_TEXT_MESSAGE)
                    chatHeadList.add(this.chatHeadList?.get(i)!!)
                }
                //再加自己的
                chatMessageList.add(edtMessage.text.toString())
                chatTagList.add(ChatRecyclerViewAdapter.RIGHT_TEXT_MESSAGE)
                chatHeadList.add(constant.userHead)
                saveChatRecordAndRefresh(chatMessageList,chatTagList,chatHeadList)
                edtMessage.setText("")
            }
            R.id.ivBack-> finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==1 && resultCode==1 && data?.getStringArrayListExtra("result")!=null){
            val chatMessageList1 =reversed(data.getStringArrayListExtra("result"))
            val chatMessageList=ArrayList<String>()
            val chatTagList = ArrayList<Int>()
            val chatHeadList = ArrayList<Int>()
            for(i in 0 until this.chatHeadList!!.size+1){
                chatMessageList.addAll(chatMessageList1)
                if(i<this.chatHeadList!!.size) {
                    for (j in 0 until chatMessageList1.size) {//加除了自己的
                        chatHeadList.add(this.chatHeadList!![i])
                    }
                }else {//加自己的
                    for (j in 0 until chatMessageList1.size) {
                        chatHeadList.add(constant.userHead)
                    }
                }
                if(i<this.chatHeadList!!.size){
                    for(j in 0 until chatMessageList1.size){//加除了自己的
                        chatTagList.add(ChatRecyclerViewAdapter.LEFT_IMG_MESSAGE)
                    }
                }else  for(j in 0 until chatMessageList1.size){//加自己的
                    chatTagList.add(ChatRecyclerViewAdapter.RIGHT_IMG_MESSAGE)
                }
            }
            saveChatRecordAndRefresh(chatMessageList,chatTagList,chatHeadList)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun reversed(list : ArrayList<String>): ArrayList<String>{ //逆个序
        for(i in 0 until list.size/2){
            val temp=list[i]
            list[i]=list[list.size-1-i]
            list[list.size-1-i]=temp
        }
        return list
    }

    inner class SpaceItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val d = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics)
            outRect.left = d.toInt()
            outRect.right = d.toInt()
            outRect.top = d.toInt() / 2
            outRect.bottom = d.toInt() / 2
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {//最后一项
                outRect.top = d.toInt()
            }
            if (parent.getChildAdapterPosition(view) == 0) {//第一项
                outRect.bottom = d.toInt()
            }

        }
    }



    override fun onScale(xPrecent: Float, yPrecent: Float, url: String) {
        mxPrecent = xPrecent
        myPrecent = yPrecent
        val animation =
            ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, xPrecent, Animation.RELATIVE_TO_SELF, yPrecent)
        animation.duration = 200
        animation.fillAfter = true
        ivScale.animation = animation
        ivScale.visibility = View.VISIBLE
        ivScale.postDelayed({//动画完了再加载
            Glide.with(this).load(url).into(ivScale)
        },200)
    }

    private fun hideScaleImg(){
        val animation = ScaleAnimation(
            1f,
            0f,
            1f,
            0f,
            Animation.RELATIVE_TO_SELF,
            mxPrecent!!,
            Animation.RELATIVE_TO_SELF,
            myPrecent!!
        )
        animation.duration = 200
        ivScale.animation = animation
        ivScale.visibility = View.GONE
        ivScale.postDelayed({//动画完了再清空图片
            Glide.with(this).load("").error(Color.BLACK).into(ivScale)
        },200)
    }


    private fun saveChatRecordAndRefresh(chatMessageList: ArrayList<String>, chatTagList: ArrayList<Int>, chatHeadList: ArrayList<Int>){
        val time=System.currentTimeMillis()
        if (time - lastTime > 1000 * 5 * 60) {//比上一次聊天时间大于五分钟聊天才加聊天的时间
            chatMessageList.add(time.toString())
            chatTagList.add(ChatRecyclerViewAdapter.TIME)
            chatHeadList.add(0)
            lastTime = System.currentTimeMillis()
            chat?.lastChatTime=lastTime.toString()
        }
        if(chatTagList[0]==ChatRecyclerViewAdapter.RIGHT_IMG_MESSAGE || chatTagList[0]==ChatRecyclerViewAdapter.LEFT_IMG_MESSAGE){
            chat?.lastMessage="[图片]"
        }else  chat?.lastMessage=chatMessageList[0]
        message?.chatMessageList?.addAll(0,chatMessageList)
        message?.chatTagList?.addAll(0,chatTagList)
        message?.chatHeadList?.addAll(0,chatHeadList)
        SPHelp.getInstance(this).saveMessage(message,chatUserId)

        //更新外面列表
        val chatList= SPHelp.getInstance(this).getChatList()
        chatList.removeAt(chat!!.position)
        for (i in 0 until chat!!.position) {
            chatList.get(i).position = chatList.get(i).position + 1
        }
        chat!!.position = 0
        chatList.add(0, chat!!)
        SPHelp.getInstance(this).setChatList(chatList)

        adapter?.addMessage(chatMessageList,chatTagList,chatHeadList)
        hasChange=true
    }



    override fun onBackPressed() {
        if (ivScale.visibility == View.VISIBLE) {
            hideScaleImg()
        }else if(hasChange || newGroupChat){
            setResult(1)
            super.onBackPressed()
        }else super.onBackPressed()
    }
}
