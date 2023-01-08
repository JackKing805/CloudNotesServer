package com.cool.cloudnotesserver.requset.model

import com.blankj.utilcode.util.GsonUtils

data class ResponseMessage(
    val code:Int,
    val message:String,
    val body:Any?=null
){
    fun toJson():String{
        return GsonUtils.toJson(this)
    }
}