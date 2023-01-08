package com.cool.cloudnotesserver.extensions

import com.blankj.utilcode.util.GsonUtils

fun ByteArray.toKotlinString() = String(this)

inline fun <reified T> ByteArray.toObject() = try {
    GsonUtils.fromJson(toKotlinString(),T::class.java)
}catch (e:Exception){
    e.printStackTrace()
    null
}