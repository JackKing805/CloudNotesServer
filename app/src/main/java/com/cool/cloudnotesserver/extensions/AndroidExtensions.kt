package com.cool.cloudnotesserver.extensions

fun <T> List<T>.safeSubList(start:Int,sizeu:Int):List<T>{
    if (sizeu<=0){
        return emptyList()
    }

    if (start>=size){
        return emptyList()
    }

    val end = if (start+sizeu-1>=size){
        size
    }else{
        start+sizeu
    }
    return subList(start,end)
}