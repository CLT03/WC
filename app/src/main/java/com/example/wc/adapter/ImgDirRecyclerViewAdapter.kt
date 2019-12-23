package com.example.wc.adapter


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.wc.R
import kotlinx.android.synthetic.main.item_img_select_dir.view.*
import java.util.*

class ImgDirRecyclerViewAdapter(var context:Context?, var dirSelectListener: DirSelectListener?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listFile: ArrayList<ArrayList<String>> = ArrayList()
    private var currentPosition:Int=0
    private var lastSelectYesImg:ImageView?=null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    public fun setData(listFile: ArrayList<ArrayList<String>>){
         this.listFile=listFile
    }


    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_img_select_dir, p0, false))
    }


    override fun getItemCount(): Int {
        return if(listFile.size>0) listFile[0].size else 0
    }




    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Glide.with(context).load(listFile[0][position]).centerCrop().into(holder.itemView.ivContent)
        holder.itemView.tvDir.text=listFile[1][position]
        holder.itemView.tvNumber.text="("+listFile[2][position]+")"
        if(currentPosition==position)
            holder.itemView.ivSelect.visibility=View.VISIBLE
        else  holder.itemView.ivSelect.visibility=View.GONE
        holder.itemView.cl.setOnClickListener {
            lastSelectYesImg?.visibility=View.GONE
            lastSelectYesImg=holder.itemView.ivSelect
            holder.itemView.ivSelect.visibility=View.VISIBLE
            dirSelectListener?.onSelect(position)
            currentPosition=position
        }

    }

    public interface DirSelectListener{
       public fun onSelect(index:Int)
    }




}