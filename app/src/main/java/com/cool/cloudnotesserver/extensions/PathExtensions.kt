package com.cool.cloudnotesserver.extensions

import java.net.URI
import java.net.URL

fun String.matchUrlPath(localRegisterPath:String):Boolean{
    val url = URL(this)
    return url.path==localRegisterPath
}

fun URI.matchUrlPath(localRegisterPath:String):Boolean{
    return path==localRegisterPath
}

fun String?.parameterToArray():Map<String,String>{
    return if (this == null){
        emptyMap()
    }else{
        val map = mutableMapOf<String,String>()
        this.split("&").forEach {
            val split = it.split("=")
            map[split[0]] = split[1]
        }
        map.toMap()
    }
}