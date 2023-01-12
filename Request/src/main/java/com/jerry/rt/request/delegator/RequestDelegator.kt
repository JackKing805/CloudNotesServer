package com.jerry.rt.request.delegator

import android.content.Context
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.request.RequestUtils
import com.jerry.rt.request.bean.ParameterBean
import com.jerry.rt.request.extensions.*
import com.jerry.rt.request.factory.RequestFactory
import com.jerry.rt.additation.interfaces.IResourcesDispatcher
import com.jerry.rt.additation.configuration.DefaultResourcesDispatcher
import com.jerry.rt.request.utils.ResponseUtils

/**
 * 请求分发
 */
internal object RequestDelegator {
    private var resourcesDispatcher: IResourcesDispatcher = DefaultResourcesDispatcher()

    fun setResourcesDispatcher(dispatcher: IResourcesDispatcher){
        this.resourcesDispatcher = dispatcher
    }

    fun init(controllers:MutableList<Class<*>>){
        RequestFactory.init(controllers)
    }

    internal fun dispatcher(context: Context, request: Request, response: Response) {
        val requestURI = request.getPackage().getRequestURI()
        RequestUtils.getIRequestListener()?.onRequest(requestURI.path?:"")
        if (!RequestFactory.onRequest(context, request,response)){
            return
        }
        val controllerMapper = RequestFactory.matchController(requestURI.path)
        if (controllerMapper != null) {
            if (controllerMapper.requestMethod.content.equals(request.getPackage().method, true)) {
                val newInstance = controllerMapper.instance
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
                    ResponseUtils.dispatcherError(context,request,response, 500)
                    return
                }
                try {
                    val invoke = controllerMapper.method.invoke(newInstance, *p.toTypedArray())
                    if (invoke==null){
                        ResponseUtils.dispatcherError(context,request,response, 500)
                    }else{
                        ResponseUtils.dispatcherReturn(context, controllerMapper.isRestController,request, response, invoke)
                    }
                } catch (e: NullPointerException) {
                    ResponseUtils.dispatcherError(context,request,response, 502)
                }
                return
            } else {
                ResponseUtils.dispatcherError(context,request,response, 405)
                return
            }
        }
        ResponseUtils.dispatcherError(context,request,response, 404)
    }
}

