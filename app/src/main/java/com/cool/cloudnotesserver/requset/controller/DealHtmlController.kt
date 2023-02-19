package com.cool.cloudnotesserver.requset.controller

import android.util.Log
import com.jerry.request_base.annotations.Controller
import com.jerry.request_base.bean.RequestMethod
import com.jerry.request_core.constants.FileType
import com.jerry.request_shiro.shiro.ShiroUtils
import com.jerry.request_shiro.shiro.impl.SimpleUserLogin
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.core.http.protocol.RtContentType


@Controller
class DealHtmlController {

    @Controller("/")
    fun tiktokRoot(request: Request,response: Response):String{
        val authInfo = try {
            ShiroUtils.getAuthInfo(request)
        }catch (e:Exception){
            null
        }
        return if (authInfo==null){
            FileType.ASSETS.content + "login.html"
        }else {
            FileType.ASSETS.content + "index.html"
        }
    }
}