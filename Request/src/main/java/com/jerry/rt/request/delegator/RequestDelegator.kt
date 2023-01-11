package com.jerry.rt.request.delegator

import android.content.Context
import android.os.Environment
import com.blankj.utilcode.util.GsonUtils
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.core.http.protocol.RtCode
import com.jerry.rt.core.http.protocol.RtContentType
import com.jerry.rt.request.RequestUtils
import com.jerry.rt.request.bean.ParameterBean
import com.jerry.rt.request.constants.FileType
import com.jerry.rt.request.extensions.*
import com.jerry.rt.request.factory.RequestFactory
import com.jerry.rt.request.interfaces.IResourcesDispatcher
import com.jerry.rt.request.configuration.DefaultResourcesDispatcher
import java.io.File

/**
 * 请求分发
 */
internal object RequestDelegator {
    private val rootDir = Environment.getExternalStorageDirectory().absolutePath
    private var resourcesDispatcher:IResourcesDispatcher = DefaultResourcesDispatcher()

    fun setResourcesDispatcher(dispatcher: IResourcesDispatcher){
        this.resourcesDispatcher = dispatcher
    }

    fun init(controllers:MutableList<Class<*>>){
        RequestFactory.init(controllers)
    }

    internal fun dispatcher(context: Context, request: Request, response: Response) {
        if (!RequestFactory.onRequest(context, request,response)){
            return
        }
        val requestURI = request.getPackage().getRequestURI()
        RequestUtils.getIRequestListener()?.onRequest(requestURI.path?:"")


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
                    dispatcherError(context,request,response, 500)
                    return
                }
                try {
                    val invoke = controllerMapper.method.invoke(newInstance, *p.toTypedArray())
                    dispatcherReturn(context, controllerMapper.isRestController,request, response, invoke)
                } catch (e: NullPointerException) {
                    dispatcherError(context,request,response, 502)
                }
                return
            } else {
                dispatcherError(context,request,response, 405)
                return
            }
        }
        dispatcherError(context,request,response, 404)
    }

    private fun dispatcherError(context: Context,request: Request,response: Response, errorCode: Int) {
        if (!RequestFactory.onResponse(context, request, response)){
            return
        }
        response.setStatusCode(errorCode)
        response.write(RtCode.match(errorCode).message, RtContentType.TEXT_HTML.content)
    }

    private fun dispatcherReturn(
        context: Context,
        isRestController: Boolean,
        request: Request,
        response: Response,
        returnObject: Any?
    ) {
        if (!RequestFactory.onResponse(context,request,response)){
            return
        }
        if (returnObject == null) {
            dispatcherError(context,request,response, 500)
            return
        }
        if (returnObject is Unit) {
            dispatcherError(context,request,response, 500)
        } else {
            if (isRestController) {
                if (returnObject is String) {
                    response.write(returnObject, RtContentType.JSON.content)
                } else {
                    response.write(GsonUtils.toJson(returnObject), RtContentType.JSON.content)
                }
            } else {
                if (returnObject is String) {
                    val fileType = FileType.matchFileType(returnObject)
                    if (fileType==null){
                        if(returnObject.startsWith("{")&& returnObject.endsWith("}")){
                            response.write(returnObject, RtContentType.JSON.content)
                        }else {
                            response.write(returnObject, returnObject.getFileMimeType())
                        }
                    }else{
                        when(fileType.fileType){
                            FileType.SD_CARD -> {
                                if (fileType.str.startsWith(rootDir)){
                                    response.writeFile(File(fileType.str))
                                }else{
                                    response.writeFile(File(rootDir,fileType.str))
                                }
                            }
                            FileType.ASSETS -> {
                                val byteArrayFromAssets = fileType.str.byteArrayFromAssets()
                                if (byteArrayFromAssets!=null){
                                    response.write(byteArrayFromAssets,fileType.str.getFileMimeType())
                                }else{
                                    dispatcherError(context,request,response,404)
                                }
                            }
                            FileType.APP_FILE -> {
                                response.writeFile(File(RequestUtils.getApplication().filesDir,fileType.str))
                            }
                            FileType.RAW -> {
                                val raw = fileType.str.toInt().byteArrayFromRaw()
                                if (raw!=null){
                                    response.write(raw,fileType.str.getFileMimeType())
                                }else{
                                    dispatcherError(context,request,response,404)
                                }
                            }
                        }
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

