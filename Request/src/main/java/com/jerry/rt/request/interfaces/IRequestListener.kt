package com.jerry.rt.request.interfaces

import android.content.Context
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.request.constants.Status

interface IRequestListener {
    fun onStatusChange(status:Status)

    fun onRequest(url:String){}

    sealed class AuthResult{
        object Grant :AuthResult()
        data class Denied(val result: Any):AuthResult()
    }
}