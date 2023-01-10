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
import com.jerry.rt.request.anno.Controller
import com.jerry.rt.request.anno.RequestMethod
import com.jerry.rt.request.extensions.*
import com.jerry.rt.request.interfaces.IRequestListener
import com.jerry.rt.request.interfaces.IResourcesDispatcher
import com.jerry.rt.request.interfaces.impl.DefaultResourcesDispatcher
import java.io.File
import java.lang.reflect.Method

/**
 * 请求分发
 */
internal object RequestDelegator {
    private val map = mutableMapOf<String, ControllerMapper>()
    private var resourcesDispatcher:IResourcesDispatcher = DefaultResourcesDispatcher()
    private val rootDir = Environment.getExternalStorageDirectory().absolutePath

    fun setResourcesDispatcher(dispatcher: IResourcesDispatcher){
        this.resourcesDispatcher = dispatcher
    }

    fun init(controllers:MutableList<Class<*>>){
        controllers.forEach {
            it.getAnnotation(Controller::class.java)?.let { cc ->
                val clazzNeedAuth = cc.needAuth
                val isClassJson = cc.isRest
                val clazzPath = cc.value
                val clazzEndIsLine = clazzPath.endsWith("/")
                it.methods.forEach { m ->
                    m.getAnnotation(Controller::class.java)?.let { mc ->
                        val methodNeedAuth = mc.needAuth
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
                        map[fullPath] = ControllerMapper(it, m, mc.requestMethod, isClassJson or isMethodJson, needAuth = clazzNeedAuth or methodNeedAuth)
                    }
                }
            }
        }
    }

    internal fun dispatcher(context: Context, request: Request, response: Response) {
        val requestURI = request.getPackage().getRequestURI()
        RequestUtils.getIRequestListener()?.onRequest(requestURI.path?:"")
        if (requestURI.isResources()){
            val auth = auth(context, request, response)
            if (auth is IRequestListener.AuthResult.Denied){
                dispatcherError(response,403)
                return
            }else{
                val dealResources = resourcesDispatcher.dealResources(context, request, response,requestURI.resourcesName())
                dispatcherReturn(context,false,response,dealResources)
            }
        }else{
            val controllerMapper = map[requestURI.path]
            if (controllerMapper != null) {
                if (controllerMapper.requestMethod.content.equals(request.getPackage().method, true)) {
                    if (controllerMapper.needAuth){
                        val auth = auth(context, request, response)
                        if (auth is IRequestListener.AuthResult.Denied){
                            dispatcherReturn(context,controllerMapper.isRestController,response,auth.result)
                            return
                        }
                    }
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
                                    dispatcherError(response,404)
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
                                    dispatcherError(response,404)
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

    private fun auth(context: Context,request: Request,response:Response):IRequestListener.AuthResult{
        return RequestUtils.getIRequestListener()?.onAuth(context,request,response)?:IRequestListener.AuthResult.Grant
    }
}

data class ControllerMapper(
    val clazz: Class<*>,
    val method: Method,
    val requestMethod: RequestMethod,
    val isRestController: Boolean,
    val needAuth:Boolean
)