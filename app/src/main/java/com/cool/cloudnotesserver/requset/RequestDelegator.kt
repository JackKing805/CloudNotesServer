package com.cool.cloudnotesserver.requset

import android.content.Context
import com.cool.cloudnotesserver.requset.controller.RootController
import com.cool.cloudnotesserver.requset.interfaces.Controller
import com.cool.cloudnotesserver.requset.interfaces.RequestMethod
import com.cool.cloudnotesserver.requset.model.ResponseMessage
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.core.http.protocol.RtCode
import com.jerry.rt.core.http.protocol.RtContentType
import java.lang.reflect.Method

/**
 * 请求分发
 */
object RequestDelegator {
    private val list = mutableListOf(
        RootController::class.java,
    )

    private val map = mutableMapOf<String, ControllerMapper>()

    init {
        list.forEach {
            it.getAnnotation(Controller::class.java)?.let { cc ->
                val clazzPath = cc.value
                it.methods.forEach { m ->
                    m.getAnnotation(Controller::class.java)?.let { mc ->
                        val methodPath = if (mc.value.startsWith("/")) {
                            mc.value.substring(1)
                        } else {
                            mc.value
                        }
                        val fullPath = clazzPath + methodPath
                        map[fullPath] = ControllerMapper(it, m,mc.requestMethod)
                    }
                }
            }
        }
    }

    fun dispatcher(context: Context, request: Request, response: Response) {
        val requestURI = request.getPackage().getRequestURI()
        val controllerMapper = map[requestURI.path]
        if (controllerMapper != null) {
            if (controllerMapper.requestMethod.content.equals(request.getPackage().method,true)){
                val newInstance = controllerMapper.clazz.newInstance()
                val p = mutableListOf<Any>()
                controllerMapper.method.genericParameterTypes.forEach {
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
                controllerMapper.method.invoke(newInstance, *p.toTypedArray())
                return
            }else{
                response.setStatusCode(405)
                response.write("Not support method", RtContentType.TEXT_HTML.content)
                return
            }
        }

        response.setStatusCode(404)
        response.write("404", RtContentType.TEXT_HTML.content)
    }
}

data class ControllerMapper(val clazz: Class<*>, val method: Method, val requestMethod:RequestMethod)