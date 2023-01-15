package com.cool.cloudnotesserver.extensions

fun <T> List<T>.safeSubList(start:Int,sizeu:Int):List<T>{
    if (start>=size){
        return emptyList()
    }

    val end = if (start+sizeu>=size){
        size
    }else{
        start+sizeu
    }
    return subList(start,end)
}