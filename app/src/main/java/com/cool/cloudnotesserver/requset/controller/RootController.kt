package com.cool.cloudnotesserver.requset.controller

import android.content.Context
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.GsonUtils
import com.cool.cloudnotesserver.db.ServerRoom
import com.cool.cloudnotesserver.db.entity.User
import com.cool.cloudnotesserver.db.service.ServiceRoomService
import com.cool.cloudnotesserver.extensions.log
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.cool.cloudnotesserver.requset.model.ResponseMessage
import com.jerry.request_base.annotations.Controller
import com.jerry.request_base.bean.RequestMethod
import com.jerry.request_core.anno.ParamsQuery
import com.jerry.request_core.constants.FileType
import com.jerry.request_core.extensions.toObject
import com.jerry.request_shiro.shiro.ShiroUtils
import com.jerry.request_shiro.shiro.impl.SimpleUserLogin
import com.jerry.rt.core.http.protocol.RtContentType
import okhttp3.OkHttpClient
import java.io.File
import java.util.*

@Controller("/")
class RootController {
    @Controller("/computer/video/list")
    fun onVideoList():String{
//        http://192.168.101.8:8080/video/list
        val okHttpClient = OkHttpClient()
        val request = okhttp3.Request.Builder()
            .url("http://192.168.101.8:8080/video/list")
            .get()
            .build()
        val execute = okHttpClient.newCall(request).execute()
        val string = execute.body?.string()?:""
        return string
    }

    @Controller("/computer/video/{name}")
    fun playVideo(response: Response,@ParamsQuery("name") name:String?):String{
        val okHttpClient = OkHttpClient()
        val request = okhttp3.Request.Builder()
            .url("http://192.168.101.8:8080/video/list")
            .get()
            .build()
        val execute = okHttpClient.newCall(request).execute()
        val string = execute.body?.string()?:""
        val responseMessage = try {
            GsonUtils.fromJson(string,ResponseMessage::class.java)
        }catch (e:Exception){
            e.printStackTrace()
            null
        }

        response.setContentType(RtContentType.TEXT_HTML.content)
        if (responseMessage==null){
            return "Error"
        }else{
            val list= responseMessage.body as List<String>
            val urls = mutableListOf<String>()
            list.forEach {
                val url = "http://192.168.101.8:8080/video/handle/play?name=$it"
                urls.add(url)
            }

            val prex = """
                         <!DOCTYPE html>
                        <html lang="zh-CN">
                        <head>
                          <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
                          <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
                          <meta name="renderer" content="webkit">
                          <meta name="viewport" content="width=device-width, maximum-scale=2">
                          <title>CloudNote</title>
                        </head>
                        <body>
            """.trimIndent()

            val body = StringBuilder("")
            urls.forEach {
                body.append("\r\n").append(
                    "<video width=\"100%\" height=\"240\" controls src=\"$it\"></video>"
                ).append("\r\n")
            }


            val end = """
            </body>
            </html>
            """.trimIndent()

            return prex + body.toString() + end
        }
    }

    @Controller("/html/{page}")
    fun onRootRequest(context: Context, request: Request, response: Response,page:String?):String {
        if (page==null){
            return FileType.ASSETS.content + "index.html"
        }else{
            return FileType.ASSETS.content + page
        }
    }

    data class UserRequest(
        val username:String,
        val password:String
    )

    @Controller(value = "/login",requestMethod = RequestMethod.POST, isRest = true)
    fun onLoginRequest(context: Context, request: Request, response: Response, userRequest: UserRequest?): ResponseMessage {
        if (userRequest==null){
            return ResponseMessage.error("need login parameter")
        }
        val userDao = ServerRoom.instance.getUserDao()
        val findByUserName = userDao.findByUserName(userRequest.username)
        if (findByUserName==null){
            return ResponseMessage.error("username not exits")
        }else{
            val thisHashedCredential = EncryptUtils.encryptMD5ToString(findByUserName.salt + userRequest.password)

            if (thisHashedCredential==findByUserName.hashedCredential){
                val loginToken = ShiroUtils.login(
                    SimpleUserLogin(
                        request,
                        response,
                        findByUserName.username,
                        userRequest.password
                    )
                )
                return ResponseMessage.ok("login success",loginToken)
            }else{
                return ResponseMessage.error("username or password not correct")
            }
        }
    }

    @Controller(value = "/register",requestMethod = RequestMethod.POST, isRest = true)
    fun onRegisterRequest(context: Context, request: Request, response: Response): ResponseMessage {
        val userRequest = request.getBody().toObject<UserRequest>() ?: return ResponseMessage.error("need register parameter")
        if (userRequest.username.isEmpty()){
            return ResponseMessage.error("username can't be empty")
        }

        if (userRequest.password.isEmpty()){
            return ResponseMessage.error("password can't be empty")
        }

        val userDao = ServerRoom.instance.getUserDao()
        val findByUserName = userDao.findByUserName(userRequest.username)
        if (findByUserName==null){
            val salt = UUID.randomUUID().toString()
            val hashedCredential = EncryptUtils.encryptMD5ToString(salt + userRequest.password)

            ServiceRoomService.createUser(User(username = userRequest.username, salt = salt, hashedCredential = hashedCredential, nickName = "CloudNote User"))
            return ResponseMessage.ok("register success")
        }else{
            return ResponseMessage.error("username is already be use,please change your username")
        }
    }
}