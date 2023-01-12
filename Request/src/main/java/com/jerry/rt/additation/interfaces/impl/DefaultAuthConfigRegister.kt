package com.jerry.rt.additation.interfaces.impl

import android.content.Context
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.request.anno.ConfigRegister
import com.jerry.rt.request.anno.Configuration
import com.jerry.rt.additation.interfaces.IAuthDispatcher
import com.jerry.rt.request.interfaces.IConfig

@ConfigRegister(registerClass = Any::class)
class DefaultAuthConfigRegister : IConfig() {
    private  val requestInterceptorList: MutableList<RequestInterceptor> = mutableListOf()

    override fun init(annotation: Configuration, clazz: Class<*>) {
        val newInstance = clazz.newInstance()
        clazz.methods.forEach {
            val parameters = it.parameters
            if (parameters.size==1 && RequestInterceptor::class.java.isAssignableFrom(parameters[0].type)){
                val requestInterceptor = RequestInterceptor()
                it.invoke(newInstance,requestInterceptor)
                if (!requestInterceptor.isBuild()){
                    throw IllegalStateException("please add request handler")
                }
                requestInterceptorList.add(requestInterceptor)
            }
        }
    }

    override fun onRequest(context: Context, request: Request, response: Response): Boolean {
        requestInterceptorList.forEach {
            val pass = it.hand(context, request, response)
            if (!pass){
                return false
            }
        }
        return  true
    }


    class RequestInterceptor(
        private val interceptor:MutableList<String> = mutableListOf(),
    ){
        private var requestHandler: IRequestHandler?=null

        internal fun isBuild() = requestHandler != null

        fun interceptor(url:String):RequestInterceptor{
            interceptor.add(url)
            return this
        }

        fun build(requestHandler: IRequestHandler){
            this.requestHandler = requestHandler
        }

        internal fun hand(context: Context,request: Request,response: Response):Boolean{
            val requestURI = request.getPackage().getRequestURI()
            val path = requestURI.path?:""
            interceptor.filter {
                path == it || path.startsWith(it)
            }.forEach { _ ->
                val pass = requestHandler!!.handle(context, request, response)
                if (!pass){
                    return  false
                }
            }
            return true
        }
    }

    interface IRequestHandler{
        fun handle(context: Context,request: Request,response: Response):Boolean
    }
}