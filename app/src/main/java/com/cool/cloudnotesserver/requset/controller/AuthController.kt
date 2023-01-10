package com.cool.cloudnotesserver.requset.controller

import android.content.Context
import com.cool.cloudnotesserver.extensions.log
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.request.anno.Controller
import okhttp3.Response

//需要认证才能访问的controller
@Controller("/auth")
class AuthController {
    @Controller("/file")
    fun onFileRequest(context: Context, request: Request, response: Response) {
        "onFileRequest".log()
        val requestURI = request.getPackage().getRequestURI()

    }
}