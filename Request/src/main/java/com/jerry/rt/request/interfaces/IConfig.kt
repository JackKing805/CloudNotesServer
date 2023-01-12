package com.jerry.rt.request.interfaces

import android.content.Context
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.request.anno.Configuration

/**
 * 配置注册类，需要搭配ConfigRegister 注解同时使用
 */
abstract class IConfig {
    abstract fun init(annotation: Configuration,clazz: Class<*>)

    abstract fun onRequest(context: Context,request: Request,response: Response):Boolean
}