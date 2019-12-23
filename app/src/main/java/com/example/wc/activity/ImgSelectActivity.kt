package com.example.wc.activity

import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import com.example.wc.adapter.ImgDirRecyclerViewAdapter
import com.example.wc.adapter.ImgRecyclerViewAdapter
import com.example.wc.R
import kotlinx.android.synthetic.main.activity_img_select.*
import java.text.SimpleDateFormat
import java.util.*


class ImgSelectActivity : AppCompatActivity(), View.OnClickListener {
    private val listDirname = ArrayList<String>()  //目录
    private val listDirFirstPath = ArrayList<String>()  //目录下的第一张图片链接
    private var listSelectResult = ArrayList<String>()  //选择结果
    private val listNumber = ArrayList<String>()  //目录下的图片数量
    private var imgAdapter : ImgRecyclerViewAdapter?=null
    private var dirAdapter : ImgDirRecyclerViewAdapter?=null
    private var canClick: Boolean = false  //展开目录是否可点击
    private var mShowAction: TranslateAnimation?=null //目录show动画
    private var mHiddenAction: TranslateAnimation?=null  //目录hide动画

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_img_select)
        window.statusBarColor = Color.parseColor("#333333")
        ivCancel.setOnClickListener(this)
        tvDir.setOnClickListener(this)
        ivDir.setOnClickListener(this)
        tvSend.setOnClickListener(this)
        viewCover.setOnClickListener(this)
        val gridLayoutManager = GridLayoutManager(this, 4)
        recyclerView.layoutManager=gridLayoutManager //列表布局
        imgAdapter = ImgRecyclerViewAdapter(this,
            object : ImgRecyclerViewAdapter.ImgSelectListener {
                override fun onSelect(number: Int, listSelectResult: ArrayList<String>) {
                    if(number>0) {
                        tvSend.text = "发送($number/9)"
                        tvSend.background=resources.getDrawable(R.drawable.r2_green_normal)
                        tvSend.setTextColor(Color.parseColor("#ffffff"))
                        tvSend.isClickable=true
                    }else{
                        tvSend.text = "发送"
                        tvSend.background=resources.getDrawable(R.drawable.r2_back_normal)
                        tvSend.setTextColor(Color.parseColor("#313131"))
                        tvSend.isClickable=false
                    }
                    this@ImgSelectActivity.listSelectResult = listSelectResult
                }
            })
        recyclerView.adapter = imgAdapter
        //设置增加或删除条目的动画
        recyclerView.itemAnimator = DefaultItemAnimator()
        val layoutManage = LinearLayoutManager(this)
        layoutManage.orientation = LinearLayoutManager.VERTICAL
        recyclerViewDir.layoutManager = layoutManage//列表布局
        //设置Adapter
        dirAdapter= ImgDirRecyclerViewAdapter(this,
            object : ImgDirRecyclerViewAdapter.DirSelectListener {
                override fun onSelect(index: Int) {
                    imgAdapter?.setPositionDir(index)
                    imgAdapter?.notifyDataSetChanged()
                    hideDir()
                    tvDir.text = if (listDirname.size > 0) listDirname[index] else "没有相关文件"
                }
            })
        recyclerViewDir.adapter = dirAdapter
        //设置增加或删除条目的动画
        recyclerViewDir.itemAnimator = DefaultItemAnimator()
        mShowAction = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        )
        mShowAction?.duration = 250
        mHiddenAction = TranslateAnimation(
            Animation.RELATIVE_TO_SELF,
            0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            -1.0f
        )
        mHiddenAction?.duration = 250
        getAllImg()
    }

    private fun getAllImg() {
        Thread {
            val projection = arrayOf(
                MediaStore.Images.Media.DATA,
                //MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DATE_MODIFIED
            )
            val cursor: Cursor?
            //全部图片
            val where = (MediaStore.Images.Media.MIME_TYPE + "=? or "
                    + MediaStore.Images.Media.MIME_TYPE + "=? or "
                    + MediaStore.Images.Media.DATA + " LIKE '%.jpg'")
            //指定格式
            val whereArgs = arrayOf("image/jpeg", "image/png")
            cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, where, whereArgs,
                MediaStore.Images.Media.DATE_MODIFIED + " desc "
            )
            //遍历
            val listFile = ArrayList<ArrayList<String>>()
            val listFileTime = ArrayList<ArrayList<String>>()
            val listAllFile= ArrayList<String>()
            while (cursor != null && cursor.moveToNext()) {
                // String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                val updateTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED))
                val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(updateTime))
                //获取图片的生成日期
                val data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA))


                val path = String(data, 0, data.size - 1)
                val dir = path.substring(0, path.lastIndexOf("/"))
                val dirname = dir.substring(dir.lastIndexOf("/") + 1, dir.length)
                if (listDirname.contains(dirname)) {//有了这个目录
                    listFile[listDirname.indexOf(dirname)].add(path)
                    listFileTime[listDirname.indexOf(dirname)].add(time)
                } else {//没有该目录就新建一个
                    listDirname.add(dirname)
                    val list1 = ArrayList<String>()
                    val list3 = ArrayList<String>()
                    listFile.add(list1)
                    listFileTime.add(list3)
                    listFile[listDirname.indexOf(dirname)].add(path)
                    listFileTime[listDirname.indexOf(dirname)].add(time)
                    listDirFirstPath.add(path)
                }
                listAllFile.add(path)
            }
            //再加一个全部图片的目录
            listFile.add(0,listAllFile)
            listDirname.add(0,"图片")
            if(listDirFirstPath.size>0) listDirFirstPath.add(0,listDirFirstPath[0])
            runOnUiThread {
                imgAdapter?.setData(listFile)
                for (i in listFile.indices) {
                    listNumber.add(listFile[i].size.toString())
                }
                imgAdapter?.notifyDataSetChanged()
                if (listDirname.size > 0) {
                    val lists = ArrayList<ArrayList<String>>()
                    lists.add(listDirFirstPath)//第一个图片的路径
                    lists.add(listDirname)//目录
                    lists.add(listNumber)//目录下的图片数量
                    dirAdapter?.setData(lists)
                    dirAdapter?.notifyDataSetChanged()
                }
                tvDir.text = if (listDirname.size > 0) listDirname[0] else "没有相关文件"
                canClick = true
            }
            cursor?.close()
        }.start()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivCancel -> {
                finish()
                overridePendingTransition(0, R.anim.anim_out)
            }
            R.id.tvDir, R.id.ivDir -> {
                if (canClick && listDirname.size > 0) {
                     if(recyclerViewDir.visibility==View.VISIBLE){
                         hideDir()
                     }else{
                         showDir()
                     }
                }
            }
            R.id.tvSend -> {
                setResult(1, Intent().putStringArrayListExtra("result", listSelectResult))
                finish()
            }
            R.id.viewCover,R.id.frameLayout -> hideDir()
        }
    }

    private fun showDir(){
        frameLayout.visibility = View.VISIBLE
        recyclerViewDir.startAnimation(mShowAction)
        recyclerViewDir.visibility = View.VISIBLE
        viewCover.visibility=View.VISIBLE
        ivDir.rotation = 180f
    }

    private fun hideDir(){
        frameLayout.visibility = View.VISIBLE
        recyclerViewDir.startAnimation(mHiddenAction)
        recyclerViewDir.visibility = View.GONE
        viewCover.visibility=View.GONE
        ivDir.rotation = 0f
    }

    override fun onBackPressed() {
        if(recyclerViewDir.visibility==View.VISIBLE){
            hideDir()
        }else {
            finish()
            overridePendingTransition(0, R.anim.anim_out)
        }
    }
}
