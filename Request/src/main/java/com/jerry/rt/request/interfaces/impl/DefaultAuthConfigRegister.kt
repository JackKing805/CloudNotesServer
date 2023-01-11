package com.jerry.rt.request.interfaces.impl

import android.content.Context
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.request.anno.ConfigRegister
import com.jerry.rt.request.anno.Configuration
import com.jerry.rt.request.interfaces.IAuthDispatcher
import com.jerry.rt.request.interfaces.IConfig

@ConfigRegister
class DefaultAuthConfigRegister : IConfig() {
    private lateinit var iAuthDispatcher: IAuthDispatcher

    override fun init(annotation: Configuration, clazzInstance: Any) {
        iAuthDispatcher = clazzInstance as IAuthDispatcher
    }

    override fun determineClazz(clazz: Class<*>): Boolean {
        return IAuthDispatcher::class.java.isAssignableFrom(clazz)
    }

    override fun onRequest(context: Context, request: Request, response: Response): Boolean {
        return  iAuthDispatcher.onAuth(context,request,response)
    }

    override fun onResponse(context: Context, request: Request, response: Response): Boolean {
        return true
    }

}