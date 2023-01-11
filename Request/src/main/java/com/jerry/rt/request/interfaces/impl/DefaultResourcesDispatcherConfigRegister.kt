package com.jerry.rt.request.interfaces.impl

import android.content.Context
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.request.anno.ConfigRegister
import com.jerry.rt.request.anno.Configuration
import com.jerry.rt.request.extensions.*
import com.jerry.rt.request.factory.dispatcherReturn
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
            val dealResources = iResourcesDispatcher.dealResources(
                context,
                request,
                response,
                requestURI.resourcesName()
            )
            response.dispatcherReturn(context,false,request,dealResources)
            return false
        }
        return true
    }

}