package com.jerry.rt.request.factory

import android.content.Context
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.request.anno.Configuration
import com.jerry.rt.request.anno.Controller
import com.jerry.rt.request.anno.RequestMethod
import com.jerry.rt.request.configuration.DefaultAuthDispatcher
import com.jerry.rt.request.configuration.DefaultResourcesDispatcher
import com.jerry.rt.request.extensions.IsIConfigResult
import com.jerry.rt.request.extensions.isIConfig
import com.jerry.rt.request.interfaces.IConfig
import com.jerry.rt.request.interfaces.impl.DefaultAuthConfigRegister
import com.jerry.rt.request.interfaces.impl.DefaultResourcesDispatcherConfigRegister
import java.lang.reflect.Method


/**
 * configRegister 会提前注册
 */
internal object RequestFactory {
    private val controllerMap = mutableMapOf<String, ControllerMapper>()
    private val configRegisterList = mutableListOf<IConfig>()


    private val defaultInjects = mutableListOf<Class<*>>(
        DefaultAuthConfigRegister::class.java,
        DefaultAuthDispatcher::class.java,
        DefaultResourcesDispatcherConfigRegister::class.java,
        DefaultResourcesDispatcher::class.java
    )

    fun init(injects:MutableList<Class<*>>){
        injects.addAll(defaultInjects)
        val registers = injects.filter {
            it.isIConfig() is IsIConfigResult.Is
        }.toMutableList()
        injects.removeAll(registers)


        registers.forEach {
            registerConfigRegister(it)
        }


        injects.forEach {
            registerController(it)
            initConfiguration(it)
        }
    }

    private fun registerController(clazz:Class<*>){
        clazz.getAnnotation(Controller::class.java)?.let { cc ->
            val isClassJson = cc.isRest
            val clazzPath = cc.value
            val clazzEndIsLine = clazzPath.endsWith("/")
            clazz.methods.forEach { m ->
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
                    controllerMap[fullPath] = ControllerMapper(clazz.newInstance(), m, mc.requestMethod, isClassJson or isMethodJson)
                }
            }
        }
    }


    //注册配置注册器，需要有ConfigRegister注解并且继承子IConfig
    private fun registerConfigRegister(clazz: Class<*>){
        val iConfig = clazz.isIConfig()
        if (iConfig is IsIConfigResult.Is){
            configRegisterList.add(iConfig.instance)
        }
    }


    private fun initConfiguration(clazz: Class<*>){
        clazz.getAnnotation(Configuration::class.java)?.let { con->
            configRegisterList.forEach {
                if (it.determineClazz(clazz)){
                    it.init(con,clazz.newInstance())
                }
            }
        }
    }


    fun onRequest(context: Context,request: Request,response: Response):Boolean{
        configRegisterList.forEach {
            if (!it.onRequest(context,request,response)){
                return false
            }
        }
        return true
    }

    fun onResponse(context: Context,request: Request,response: Response):Boolean{
        configRegisterList.forEach {
            if (!it.onResponse(context,request,response)){
                return false
            }
        }
        return true
    }


    fun matchController(path:String):ControllerMapper?{
        return controllerMap[path]
    }
}

data class ControllerMapper(
    val instance: Any,
    val method: Method,
    val requestMethod: RequestMethod,
    val isRestController: Boolean
)