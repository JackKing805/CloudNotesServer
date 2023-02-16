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
class DealHtml {

    @Controller("/")
    fun tiktokRoot(request: Request,response: Response):String{
        val authInfo = try {
            ShiroUtils.getAuthInfo(request)
        }catch (e:Exception){
            null
        }
        if (authInfo==null){
            return FileType.ASSETS.content + "login.html"
        }else{
            val id = (1..18).random()
            response.setContentType(RtContentType.TEXT_HTML.content)
            return """
                <!DOCTYPE html>
                <html>
                <head>
                  <title>TikTok Video Player</title>
                  <style>
                		body {
                			margin: 0;
                			padding: 0;
                			background-color: #fff;
                			font-family: sans-serif;
                		}

                		.container {
                			display: flex;
                			flex-direction: column;
                			align-items: center;
                			justify-content: center;
                			height: 100vh;
                		}

                		video {
                			width: 90%;
                			max-width: 600px;
                			border-radius: 8px;
                			box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                		}

                		.description {
                			margin-top: 16px;
                			font-size: 20px;
                			font-weight: bold;
                			color: #444;
                		}
                	</style>
                </head>
                <body>
                <div class="container">
                  <video controls autoplay src="http://192.168.101.14:8080/video/handle/play?name=$id.mp4"></video>
                  <div class="description">Amazing TikTok Video</div>
                </div>
                </body>
                </html>

            """.trimIndent()
        }
    }


    data class HtmlLogin(
        val username:String,
        val password:String
    )

    @Controller("/html/login", requestMethod = RequestMethod.POST)
    fun login(request: Request,response: Response,htmlLogin: HtmlLogin):String{
        Log.e("ADSA",htmlLogin.toString())
        if(htmlLogin.password=="98521" && htmlLogin.username=="admin"){
            ShiroUtils.login(SimpleUserLogin(request,response, username = htmlLogin.username, password = htmlLogin.password))
            return "success"
        }else{
            throw IllegalArgumentException("login failure")
        }
    }
}