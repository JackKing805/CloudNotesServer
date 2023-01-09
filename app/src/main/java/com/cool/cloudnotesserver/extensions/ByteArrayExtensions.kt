package com.cool.cloudnotesserver.extensions

import com.blankj.utilcode.util.GsonUtils

fun ByteArray.toKotlinString() = String(this)

inline fun <reified T> ByteArray.toObject() = try {
    GsonUtils.fromJson(toKotlinString(),T::class.java)
}catch (e:Exception){
    e.printStackTrace()
    null
}

fun <T> ByteArray.toObject(clazz: Class<T>) = try {
    GsonUtils.fromJson(toKotlinString(),clazz)
}catch (e:Exception){
    e.printStackTrace()
    null
}