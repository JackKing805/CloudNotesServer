package com.cool.cloudnotesserver.requset.controller

import android.content.Context
import com.cool.cloudnotesserver.extensions.log
import com.cool.cloudnotesserver.extensions.parameterToArray
import com.cool.cloudnotesserver.requset.interfaces.Controller
import com.cool.cloudnotesserver.requset.model.ResponseMessage
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.core.http.protocol.RtContentType

//需要认证才能访问的controller

@Controller("/auth")
class AuthController {
    @Controller("/file")
    fun onFileRequest(context: Context, request: Request, response: Response) {
        "onFileRequest".log()
        val requestURI = request.getPackage().getRequestURI()
        val parameterToArray = requestURI.query.parameterToArray()
        parameterToArray["filename"]?.let {
            response.write(ResponseMessage(200,"success","").toJson(), RtContentType.JSON.content)
        }?: kotlin.run {
            response.write(ResponseMessage(-200,"please send filename").toJson(), RtContentType.JSON.content)
        }
    }
}