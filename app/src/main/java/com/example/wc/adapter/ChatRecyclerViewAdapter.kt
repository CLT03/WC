package com.example.wc.adapter


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_chat_time.view.*
import kotlinx.android.synthetic.main.item_left_message.view.*
import kotlinx.android.synthetic.main.item_right_message.view.*
import java.text.SimpleDateFormat
import java.util.*
import android.util.TypedValue
import com.example.wc.R
import com.example.wc.util.RoundTransformation
import com.example.wc.util.constant
import kotlinx.android.synthetic.main.item_chat_time.view.tvTime
import kotlinx.android.synthetic.main.item_home.view.*


class ChatRecyclerViewAdapter(var context: Context?, var imgScaleListener: ImgScaleListener?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var screenHeight: Int = 0
    private var screenWidth: Int = 0
    private var chatMessageList: ArrayList<String> = ArrayList()
    private var chatTagList: ArrayList<Int> = ArrayList()
    private var chatHeadList: ArrayList<Int> = ArrayList()
    private var imageViewMaxWH:Int=0 //聊天图片的最大宽高

    companion object {
        const val LEFT_TEXT_MESSAGE = 0  //左边文字
        const val LEFT_IMG_MESSAGE = 1   //左边图片
        const val RIGHT_TEXT_MESSAGE = 2   //右边文字
        const val RIGHT_IMG_MESSAGE = 3   //右边图片
        const val TIME = 4   //时间
    }

    init {
        val resources=context?.resources
        screenHeight = resources?.displayMetrics?.heightPixels!!- resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"))
        screenWidth = resources.displayMetrics?.widthPixels!!
        imageViewMaxWH= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,150f, resources.displayMetrics).toInt()
    }


    //添加数据的时候应该插在0位置  因为列表是倒着的
    fun addMessage(chatMessageList: ArrayList<String>, chatTagList: ArrayList<Int>, chatHeadList: ArrayList<Int>) {
        this.chatMessageList.addAll(0, chatMessageList)
        this.chatTagList.addAll(0, chatTagList)
        this.chatHeadList.addAll(0, chatHeadList)
        notifyDataSetChanged()
    }

    inner class ViewHolderLeft(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class ViewHolderRight(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class ViewHolderTime(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun getItemViewType(position: Int): Int {
        return chatTagList[position]
    }

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LEFT_TEXT_MESSAGE, LEFT_IMG_MESSAGE -> {
                val viewHolderLeft =
                    ViewHolderLeft(LayoutInflater.from(context).inflate(R.layout.item_left_message, p0, false))
                viewHolderLeft.setIsRecyclable(false)
                return viewHolderLeft
            }
            RIGHT_TEXT_MESSAGE, RIGHT_IMG_MESSAGE -> {
                return ViewHolderRight(LayoutInflater.from(context).inflate(R.layout.item_right_message, p0, false))
            }
            else ->
                ViewHolderTime(LayoutInflater.from(context).inflate(R.layout.item_chat_time, p0, false))
        }
    }


    override fun getItemCount(): Int {
        return chatTagList.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderLeft -> {//左边聊天
                Glide.with(context).load(chatHeadList[position])
                    .transform(RoundTransformation(context!! ,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5f,context!!.resources.displayMetrics).toInt()))
                    .into(holder.itemView.iv_left_head)
                if (chatTagList[position] == LEFT_TEXT_MESSAGE) {//文字
                    holder.itemView.groupLeft.visibility = View.VISIBLE
                    holder.itemView.iv_left_message.visibility = View.GONE
                    holder.itemView.tv_left_message.text = chatMessageList[position]
                } else {//图片
                    holder.itemView.groupLeft.visibility = View.GONE
                    holder.itemView.iv_left_message.visibility = View.VISIBLE
                    Glide.with(context).load(chatMessageList[position]).override(imageViewMaxWH,imageViewMaxWH).into(holder.itemView.iv_left_message)
                    holder.itemView.iv_left_message.setOnClickListener {//点击图片放大
                        val arrayOfInt: IntArray = getLocation(holder.itemView.iv_left_message)
                        imgScaleListener?.onScale(//返回图片中心位置在屏幕的比例和url
                            (arrayOfInt[0] + arrayOfInt[2].toFloat() / 2) / screenWidth,
                            (arrayOfInt[1] + arrayOfInt[3].toFloat() / 2) / screenHeight, chatMessageList[position]
                        )
                    }
                }
            }
            is ViewHolderRight -> {//右边聊天
                Glide.with(context).load(chatHeadList[position])
                    .transform(RoundTransformation(context!! ,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5f,context!!.resources.displayMetrics).toInt()))
                    .into(holder.itemView.iv_right_head)
                if (chatTagList[position] == RIGHT_TEXT_MESSAGE) {//文字
                    holder.itemView.groupRight.visibility = View.VISIBLE
                    holder.itemView.iv_right_message.visibility = View.GONE
                    holder.itemView.tv_right_message.text = chatMessageList[position]
                } else {//图片
                    holder.itemView.groupRight.visibility = View.GONE
                    holder.itemView.iv_right_message.visibility = View.VISIBLE
                    Glide.with(context).load(chatMessageList[position]).override(imageViewMaxWH,imageViewMaxWH).into(holder.itemView.iv_right_message)
                    holder.itemView.iv_right_message.setOnClickListener {
                        val arrayOfInt: IntArray = getLocation(holder.itemView.iv_right_message)
                        imgScaleListener?.onScale(
                            (arrayOfInt[0] + arrayOfInt[2].toFloat() / 2) / screenWidth,
                            (arrayOfInt[1] + arrayOfInt[3].toFloat() / 2) / screenHeight, chatMessageList[position]
                        )
                    }
                }
            }
            is ViewHolderTime -> {
                holder.itemView.tvTime.text = constant.getTime1(chatMessageList[position].toLong())
            }
        }
    }

    interface ImgScaleListener {
        public fun onScale(xPrecent: Float, yPrecent: Float, url: String)
    }

    // 获取view在屏幕的left top ,view的宽高
    private fun getLocation(v: View): IntArray {
        val loc = IntArray(4)
        val location = IntArray(2)
        v.getLocationOnScreen(location)
        loc[0] = location[0]
        loc[1] = location[1]
        val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(w, h)

        loc[2] = v.measuredWidth
        loc[3] = v.measuredHeight

        //base = computeWH();
        return loc
    }



}