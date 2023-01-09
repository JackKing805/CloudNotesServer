package com.cool.cloudnotesserver.requset

import android.content.Context
import com.blankj.utilcode.util.GsonUtils
import com.cool.cloudnotesserver.extensions.fromAssets
import com.cool.cloudnotesserver.extensions.log
import com.cool.cloudnotesserver.extensions.parameterToArray
import com.cool.cloudnotesserver.extensions.toObject
import com.cool.cloudnotesserver.requset.bean.ParameterBean
import com.cool.cloudnotesserver.requset.controller.RootController
import com.cool.cloudnotesserver.requset.interfaces.Controller
import com.cool.cloudnotesserver.requset.interfaces.RequestMethod
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.core.http.protocol.RtCode
import com.jerry.rt.core.http.protocol.RtContentType
import java.io.File
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
                val isClassJson = cc.isRest
                val clazzPath = cc.value
                val clazzEndIsLine = clazzPath.endsWith("/")
                it.methods.forEach { m ->
                    m.getAnnotation(Controller::class.java)?.let { mc ->
                        val isMethodJson = mc.isRest
                        val methodPath = if (clazzEndIsLine){
                            if (mc.value.startsWith("/")) {
                                mc.value.substring(1)
                            } else {
                                mc.value
                            }
                        }else{
                            if (mc.value.startsWith("/")) {
                                mc.value
                            } else {
                                "/"+mc.value
                            }
                        }
                        val fullPath = clazzPath + methodPath
                        map[fullPath] =
                            ControllerMapper(it, m, mc.requestMethod, isClassJson or isMethodJson)
                    }
                }
            }
        }
    }

    fun dispatcher(context: Context, request: Request, response: Response) {
        val requestURI = request.getPackage().getRequestURI()
        val controllerMapper = map[requestURI.path]
        if (controllerMapper != null) {
            if (controllerMapper.requestMethod.content.equals(request.getPackage().method, true)) {
                val newInstance = controllerMapper.clazz.newInstance()
                val p = mutableListOf<Any?>()
                try {
                    controllerMapper.method.parameters.forEach {
                        when (val clazz = it.type) {
                            Context::class.java -> {
                                p.add(context)
                            }
                            Request::class.java -> {
                                p.add(request)
                            }
                            Response::class.java -> {
                                p.add(response)
                            }
                            ParameterBean::class.java -> {
                                p.add(
                                    ParameterBean(
                                        request.getPackage()
                                            .getRequestURI().query.parameterToArray()
                                    )
                                )
                            }
                            else -> {
                                val objects = request.getBody().toObject(clazz)
                                p.add(objects)
                            }
                        }
                    }
                } catch (e: Exception) {
                    dispatcherError(response, 500)
                    return
                }
                try {
                    val invoke = controllerMapper.method.invoke(newInstance, *p.toTypedArray())
                    dispatcherReturn(context, controllerMapper.isRestController, response, invoke)
                } catch (e: NullPointerException) {
                    dispatcherError(response, 502)
                }
                return
            } else {
                dispatcherError(response, 405)
                return
            }
        }
        dispatcherError(response, 404)
    }

    private fun dispatcherError(response: Response, errorCode: Int) {
        response.setStatusCode(errorCode)
        response.write(RtCode.match(errorCode).message, RtContentType.TEXT_HTML.content)
    }

    private fun dispatcherReturn(
        context: Context,
        isRestController: Boolean,
        response: Response,
        returnObject: Any?
    ) {
        if (returnObject == null) {
            dispatcherError(response, 500)
            return
        }

        if (returnObject is Unit) {
            dispatcherError(response, 500)
        } else {
            if (isRestController) {
                if (returnObject is String) {
                    response.write(returnObject, RtContentType.JSON.content)
                } else {
                    response.write(GsonUtils.toJson(returnObject), RtContentType.JSON.content)
                }
            } else {
                if (returnObject is String) {
                    if (returnObject.endsWith(".html")) {
                        response.write(returnObject.fromAssets(), RtContentType.TEXT_HTML.content)
                    } else if (returnObject.endsWith(".xml")) {
                        response.write(returnObject.fromAssets(), RtContentType.TEXT_XML.content)
                    } else if (returnObject.endsWith(".text")) {
                        response.write(returnObject.fromAssets(), RtContentType.TEXT_PLAIN.content)
                    } else {
                        response.write(returnObject, RtContentType.TEXT_PLAIN.content)
                    }
                } else if (returnObject is File) {
                    response.writeFile(returnObject)
                } else {
                    response.write(GsonUtils.toJson(returnObject), RtContentType.TEXT_PLAIN.content)
                }
            }
        }
    }
}

data class ControllerMapper(
    val clazz: Class<*>,
    val method: Method,
    val requestMethod: RequestMethod,
    val isRestController: Boolean
)