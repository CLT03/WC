package com.example.wc.activity

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.example.wc.R
import com.example.wc.adapter.HomeRecyclerViewAdapter
import com.example.wc.util.Chat
import com.example.wc.util.SPHelp
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity(), View.OnClickListener {
    private var chatList: ArrayList<Chat>? = null
    private var adapter: HomeRecyclerViewAdapter? = null
    private var lastTime=System.currentTimeMillis()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        window.statusBarColor = Color.parseColor("#ededed")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//状态栏白底黑字
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//请求权限
            //  用户未彻底拒绝授予权限
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager//列表布局
        adapter = HomeRecyclerViewAdapter(this)
        recyclerView.adapter = adapter
        chatList = SPHelp.getInstance(this).getChatList()
        adapter?.setData(chatList)
        ivMore.setOnClickListener(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == 1) {//聊天页面返回 同步消息并刷新
            adapter?.setData(SPHelp.getInstance(this).getChatList())
        }else if (requestCode == 1 && resultCode == 2) {//发起群聊页面返回
            adapter?.setData(SPHelp.getInstance(this).getChatList())
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivMore -> startActivityForResult(Intent(this, AddGroupChatActivity::class.java), 1)
        }
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis()-lastTime>2000){
            lastTime=System.currentTimeMillis()
            Toast.makeText(this,"再按一次退出旗木卡卡西",Toast.LENGTH_SHORT).show()
        }else super.onBackPressed()
    }

}
