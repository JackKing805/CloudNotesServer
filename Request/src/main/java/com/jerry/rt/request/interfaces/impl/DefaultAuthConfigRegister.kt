package com.jerry.rt.request.interfaces.impl

import android.content.Context
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.request.anno.ConfigRegister
import com.jerry.rt.request.anno.Configuration
import com.jerry.rt.request.extensions.log
import com.jerry.rt.request.interfaces.IAuthDispatcher
import com.jerry.rt.request.interfaces.IConfig

@ConfigRegister(registerClass = IAuthDispatcher::class)
class DefaultAuthConfigRegister : IConfig() {
    private lateinit var iAuthDispatcher: IAuthDispatcher

    override fun init(annotation: Configuration, clazzInstance: Any) {
        iAuthDispatcher = clazzInstance as IAuthDispatcher
    }

    override fun onRequest(context: Context, request: Request, response: Response): Boolean {
        return  iAuthDispatcher.onAuth(context,request,response)
    }
}