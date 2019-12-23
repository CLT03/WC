package com.example.wc.adapter


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.wc.R
import kotlinx.android.synthetic.main.item_img_select.view.*
import java.util.ArrayList

class ImgRecyclerViewAdapter(var context:Context?, var imgSelectListener: ImgSelectListener?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listFile: ArrayList<ArrayList<String>> = ArrayList()
    private var listSelectResult = ArrayList<String>()  //选择结果
    private var positionDir: Int = 0  //当前目录在所有目录的位置
    private var listTextViewNumber:ArrayList<TextView> = ArrayList() //选择了的图片序号textview

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun setData(listFile: ArrayList<ArrayList<String>>){
         this.listFile=listFile
    }

    fun setPositionDir(positionDir:Int){
        this.positionDir=positionDir
    }


    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_img_select, p0, false))
    }


    override fun getItemCount(): Int {
        return if(listFile.size>0) listFile[positionDir].size else 0
    }




    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Glide.with(context).load(listFile[positionDir][position]).centerCrop().into(holder.itemView.ivContent)
        if (listSelectResult.contains(listFile[positionDir][position])) {
            holder.itemView.ivSelect.setImageDrawable(context?.resources?.getDrawable(R.drawable.r10_green_normal))
            holder.itemView.tvNumber.text=(listSelectResult.indexOf(listFile[positionDir][position])+1).toString()
            holder.itemView.viewCover.visibility=View.VISIBLE
        } else {
            holder.itemView.ivSelect.setImageDrawable(context?.resources?.getDrawable(R.mipmap.white_circle))
            holder.itemView.tvNumber.text=""
            holder.itemView.viewCover.visibility=View.GONE
        }
        holder.itemView.ivSelect.setOnClickListener {
            when {
                listSelectResult.contains(listFile[positionDir][holder.adapterPosition]) -> {//已选择过 那就取消
                    var p =listSelectResult.indexOf(listFile[positionDir][position])
                    while (p<listSelectResult.size){ //如果点击的是中间序号的图片要修改比它大的序号
                        listTextViewNumber[p].text=(p).toString()
                        p++
                    }
                    listSelectResult.remove(listFile[positionDir][holder.adapterPosition])
                    holder.itemView.ivSelect.setImageDrawable(context?.resources?.getDrawable(R.mipmap.white_circle))
                    holder.itemView.tvNumber.text=""
                    holder.itemView.viewCover.visibility=View.GONE
                    listTextViewNumber.remove(holder.itemView.tvNumber)
                }
                listSelectResult.size < 9 -> {
                    listSelectResult.add(listFile[positionDir][holder.adapterPosition])
                    val p =listSelectResult.indexOf(listFile[positionDir][position])
                    holder.itemView.ivSelect.setImageDrawable(context?.resources?.getDrawable(R.drawable.r10_green_normal))
                    holder.itemView.tvNumber.text=(p+1).toString()
                    listTextViewNumber.add(holder.itemView.tvNumber)
                    holder.itemView.viewCover.visibility=View.VISIBLE
                }
                else -> Toast.makeText(context, "你最多只能选择9个图片", Toast.LENGTH_SHORT).show()
            }
            imgSelectListener?.onSelect(listSelectResult.size,  listSelectResult)
        }
    }

    interface ImgSelectListener{
        fun onSelect(number:Int,listSelectResult:ArrayList<String>)
    }




}