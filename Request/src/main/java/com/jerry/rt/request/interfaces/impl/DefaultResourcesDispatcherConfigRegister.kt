package com.jerry.rt.request.interfaces.impl

import android.content.Context
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.request.anno.ConfigRegister
import com.jerry.rt.request.anno.Configuration
import com.jerry.rt.request.extensions.isResources
import com.jerry.rt.request.extensions.resourcesName
import com.jerry.rt.request.interfaces.IAuthDispatcher
import com.jerry.rt.request.interfaces.IConfig
import com.jerry.rt.request.interfaces.IResourcesDispatcher

@ConfigRegister
class DefaultResourcesDispatcherConfigRegister : IConfig() {
    private lateinit var iResourcesDispatcher: IResourcesDispatcher

    override fun init(annotation: Configuration, clazzInstance: Any) {
        iResourcesDispatcher = clazzInstance as IResourcesDispatcher
    }

    override fun determineClazz(clazz: Class<*>): Boolean {
        return IResourcesDispatcher::class.java.isAssignableFrom(clazz)
    }

    override fun onRequest(context: Context, request: Request, response: Response): Boolean {
        val requestURI = request.getPackage().getRequestURI()
        if (requestURI.isResources()){
            iResourcesDispatcher.dealResources(context,request,response,requestURI.resourcesName())
            return false
        }
        return true
    }

    override fun onResponse(context: Context, request: Request, response: Response): Boolean {
        return true
    }

}