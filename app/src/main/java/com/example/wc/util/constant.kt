package com.example.wc.util

import com.example.wc.R
import java.text.SimpleDateFormat
import java.util.*


class constant{
    companion object{
         val userName = "罗密欧和猪过夜" //自己的用户名
         val userHead = R.mipmap.head0 //自己的头像

         fun getTime(time:Long): String {//按格式获取时间
             val date = Date(time)
             val formatter = SimpleDateFormat("yyyy年MM月dd日 HH:mm")
             val result = formatter.format(date)
             //先获取年份
             val year = Integer.valueOf(SimpleDateFormat("yyyy").format(date))
             //获取一年中的第几天
             val day = Integer.valueOf(SimpleDateFormat("d").format(date))
             //获取当前年份 和 一年中的第几天
             val currentDate = Date(System.currentTimeMillis())
             val currentYear = Integer.valueOf(SimpleDateFormat("yyyy").format(currentDate))
             val currentDay = Integer.valueOf(SimpleDateFormat("d").format(currentDate))
             //计算 如果是去年的
             if (currentYear - year == 1) {//去年
                 //如果当前正好是 1月1日 计算去年有多少天，指定时间是否是一年中的最后一天
                 if (currentDay == 1) {
                     val yearDay: Int
                     if (year % 400 == 0) {
                         yearDay = 366//世纪闰年
                     } else if (year % 4 == 0 && year % 100 != 0) {
                         yearDay = 366//普通闰年
                     } else {
                         yearDay = 365//平年
                     }
                     if (day == yearDay) {
                         //昨天
                         return "昨天"
                     }
                     return result.substring(0, 12)
                 }
             } else if (currentYear == year) {//今年
                 if (currentDay - day == 1) {
                     //昨天
                     return "昨天"
                 }
                 if (currentDay - day == 0) {
                     //今天
                     return result.substring(12)
                 }
                 return result.substring(5, 12)
             }
             return result.substring(0, 12)
        }


        fun getTime1(time:Long): String {//按格式获取时间
            val date = Date(time)
            val formatter = SimpleDateFormat("yyyy年MM月dd日 HH:mm")
            val result = formatter.format(date)
            //先获取年份
            val year = Integer.valueOf(SimpleDateFormat("yyyy").format(date))
            //获取一年中的第几天
            val day = Integer.valueOf(SimpleDateFormat("d").format(date))
            //获取当前年份 和 一年中的第几天
            val currentDate = Date(System.currentTimeMillis())
            val currentYear = Integer.valueOf(SimpleDateFormat("yyyy").format(currentDate))
            val currentDay = Integer.valueOf(SimpleDateFormat("d").format(currentDate))
            //计算 如果是去年的
            if (currentYear - year == 1) {//去年
                //如果当前正好是 1月1日 计算去年有多少天，指定时间是否是一年中的最后一天
                if (currentDay == 1) {
                    val yearDay: Int
                    if (year % 400 == 0) {
                        yearDay = 366//世纪闰年
                    } else if (year % 4 == 0 && year % 100 != 0) {
                        yearDay = 366//普通闰年
                    } else {
                        yearDay = 365//平年
                    }
                    if (day == yearDay) {
                        //昨天
                        return "昨天"+result.substring(12)
                    }
                    return result.substring(0, 12)+getHours(result.substring(12,14))+result.substring(12)
                }
            } else if (currentYear == year) {//今年
                if (currentDay - day == 1) {
                    //昨天
                    return "昨天"+result.substring(12)
                }
                if (currentDay - day == 0) {
                    //今天
                    return result.substring(12)
                }
                return result.substring(5, 12)+getHours(result.substring(12,14))+result.substring(12)
            }
            return result.substring(0, 12)+getHours(result.substring(12,14))+result.substring(12)
        }

        private fun getHours(hour:String):String{
            return when(hour.toInt()){
                0,1,2,3,4,5-> "凌晨"
                6,7,8,9,10,11-> "上午"
                12-> "中午"
                13,14,15,16,17-> "下午"
                else-> "晚上"
            }
        }
    }
}