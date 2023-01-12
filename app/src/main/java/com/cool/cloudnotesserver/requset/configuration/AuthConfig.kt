package com.cool.cloudnotesserver.requset.configuration

import android.content.Context
import com.cool.cloudnotesserver.db.ServerRoom
import com.cool.cloudnotesserver.extensions.log
import com.cool.cloudnotesserver.requset.model.ResponseMessage
import com.jerry.rt.additation.interfaces.impl.DefaultAuthConfigRegister
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.core.http.protocol.RtContentType
import com.jerry.rt.request.anno.Configuration

//注解拦截处理器
@Configuration
class AuthConfig {
    fun lanjieUrl(requestInterceptor: DefaultAuthConfigRegister.RequestInterceptor){
        requestInterceptor.interceptor("/auth")
            .build(object :DefaultAuthConfigRegister.IRequestHandler{
                override fun handle(
                    context: Context,
                    request: Request,
                    response: Response
                ): Boolean {
                    val token = request.getPackage().getHeaderValue("Token", "")
                    if (token.isEmpty()){
                        response.write(ResponseMessage.error("not auth").toJson(),RtContentType.JSON.content)
                        return false
                    }else{
                        val userDao = ServerRoom.instance.getUserDao()
                        val findByUserToken = userDao.findByUserToken(token)
                        return if (findByUserToken==null){
                            response.write(ResponseMessage.error("token is invalid").toJson(),RtContentType.JSON.content)
                            false
                        }else{
                            true
                        }
                    }
                }
            })
    }
}