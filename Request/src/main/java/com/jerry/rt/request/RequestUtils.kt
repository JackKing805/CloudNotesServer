package com.jerry.rt.request

import android.app.Application
import com.jerry.rt.request.config.Config
import com.jerry.rt.request.delegator.RequestDelegator
import com.jerry.rt.request.interfaces.IRequestListener
import com.jerry.rt.request.interfaces.IResourcesDispatcher
import com.jerry.rt.request.service.ServerService

object RequestUtils {
    private lateinit var application: Application
    private lateinit var config: Config
    private var iRequestListener: IRequestListener?=null

    fun init(application: Application,config: Config,controllers:MutableList<Class<*>>){
        this.application = application
        this.config = config
        RequestDelegator.init(controllers)
    }

    fun startServer(){
        ServerService.run(application,true)
    }

    fun stopServer(){
        ServerService.run(application,false)
    }

    fun listen(iRequestListener: IRequestListener){
        this.iRequestListener = iRequestListener
    }

    fun setResourcesDispatcher(dispatcher: IResourcesDispatcher){
        RequestDelegator.setResourcesDispatcher(dispatcher)
    }

    internal fun getIRequestListener() = iRequestListener

    internal fun getConfig() = config

    internal fun  getApplication() = application
}