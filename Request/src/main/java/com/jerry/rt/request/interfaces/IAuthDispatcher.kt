package com.jerry.rt.request.interfaces

import android.content.Context
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response

abstract class IAuthDispatcher {
    abstract fun onAuth(context: Context,request: Request,response: Response): Boolean
}