package com.cool.cloudnotesserver.requset

import android.content.Context
import com.cool.cloudnotesserver.extensions.log
import com.cool.cloudnotesserver.extensions.matchUrlPath
import com.cool.cloudnotesserver.requset.controller.RootController
import com.cool.cloudnotesserver.requset.interfaces.Controller
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.core.http.protocol.RtContentType
import kotlinx.coroutines.*
import java.lang.reflect.Method

/**
 * 请求分发
 */
object RequestDelegator {
    private val list = mutableListOf(
        RootController::class.java,
    )

    private val map = mutableMapOf<String,ControllerMapper>()

    init {
        list.forEach {
            it.getAnnotation(Controller::class.java)?.let {  cc->
                val clazzPath = cc.value
                it.methods.forEach { m->
                    m.getAnnotation(Controller::class.java)?.let { mc->
                        val methodPath = if (mc.value.startsWith("/")){
                            mc.value.substring(1)
                        }else{
                            mc.value
                        }
                        val fullPath=clazzPath + methodPath
                        map[fullPath] = ControllerMapper(it,m)
                    }
                }
            }
        }
    }

    fun dispatcher(context: Context,request: Request,response: Response){
        val requestURI = request.getPackage().getRequestURI()
        map[requestURI.path]?.let {
            val newInstance = it.clazz.newInstance()
            val p = mutableListOf<Any>()
            it.method.genericParameterTypes.forEach {
                when (it.toString()) {
                    "class android.content.Context" -> {
                        p.add(context)
                    }
                    "class com.jerry.rt.core.http.pojo.Request" -> {
                        p.add(request)
                    }
                    "class com.jerry.rt.core.http.pojo.Response" -> {
                        p.add(response)
                    }
                    else -> {
                        throw IllegalArgumentException("not support arguments")
                    }
                }
            }
            it.method.invoke(newInstance,*p.toTypedArray())
        }?: kotlin.run {
            response.setStatusCode(404)
            response.write("404",RtContentType.TEXT_HTML.content)
        }
    }
}

data class ControllerMapper(val clazz:Class<*>,val method:Method)