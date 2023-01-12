package com.jerry.rt.additation.interfaces.impl

import android.content.Context
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.request.anno.ConfigRegister
import com.jerry.rt.request.anno.Configuration
import com.jerry.rt.request.extensions.isResources
import com.jerry.rt.request.extensions.resourcesName
import com.jerry.rt.request.interfaces.IConfig
import com.jerry.rt.additation.interfaces.IResourcesDispatcher
import com.jerry.rt.request.utils.ResponseUtils

@ConfigRegister(-1, registerClass = IResourcesDispatcher::class)
class DefaultResourcesDispatcherConfigRegister : IConfig() {
    private lateinit var iResourcesDispatcher: IResourcesDispatcher
    override fun init(annotation: Configuration, clazz: Class<*>) {
        iResourcesDispatcher = clazz.newInstance() as IResourcesDispatcher
    }


    override fun onRequest(context: Context, request: Request, response: Response): Boolean {
        val requestURI = request.getPackage().getRequestURI()
        if (requestURI.isResources()){
            val result = iResourcesDispatcher.dealResources(context,request,response,requestURI.resourcesName())
            ResponseUtils.dispatcherReturn(context,false,request,response,result)
            return false
        }
        return true
    }
}