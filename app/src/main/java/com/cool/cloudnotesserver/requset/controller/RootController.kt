package com.cool.cloudnotesserver.requset.controller

import android.content.Context
import android.os.Environment
import com.blankj.utilcode.util.SPUtils
import com.cool.cloudnotesserver.db.ServerRoom
import com.cool.cloudnotesserver.db.entity.User
import com.cool.cloudnotesserver.extensions.log
import com.cool.cloudnotesserver.extensions.toObject
import com.cool.cloudnotesserver.requset.bean.ParameterBean
import com.cool.cloudnotesserver.requset.interfaces.Controller
import com.cool.cloudnotesserver.requset.interfaces.RequestMethod
import com.cool.cloudnotesserver.requset.model.ResponseMessage
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.core.http.protocol.RtContentType
import java.io.File
import java.util.UUID

@Controller("/")
class RootController {
    @Controller("/favicon.ico")
    fun onIconRequest(context: Context, request: Request, response: Response):File {
        "onIconRequest".log()
        return File(Environment.getExternalStorageDirectory().absolutePath + File.separatorChar + "DCIM" + File.separatorChar + "Camera" + File.separatorChar + "icon.jpg")
    }

    @Controller("/")
    fun onRootRequest(context: Context, request: Request, response: Response):String {
        "onRootRequest".log()
        return "index.html"
    }

    data class UserRequest(
        val username:String,
        val password:String
    )

    @Controller(value = "/login",requestMethod = RequestMethod.POST, isRest = true)
    fun onLoginRequest(context: Context, request: Request, response: Response,userRequest: UserRequest?,parameterBean: ParameterBean):ResponseMessage{
        "onLoginRequest".log()

        if (userRequest==null){
            return ResponseMessage.error("need login parameter")
        }
        val userDao = ServerRoom.instance.getUserDao()
        val findByUserName = userDao.findByUserName(userRequest.username)
        if (findByUserName==null){
            return ResponseMessage.error("username not exits")
        }else{
            if (findByUserName.password==userRequest.password){
                val cacheToken = SPUtils.getInstance().getString(findByUserName.username, "")
                val token = cacheToken.ifEmpty {
                    UUID.randomUUID().toString()
                }
                SPUtils.getInstance().put(findByUserName.username,token)
                return ResponseMessage.ok("login success",token)
            }else{
                return ResponseMessage.error("username or password not correct")
            }
        }
    }

    @Controller(value = "/register",requestMethod = RequestMethod.POST, isRest = true)
    fun onRegisterRequest(context: Context, request: Request, response: Response):ResponseMessage{
        "onRegisterRequest".log()

        val userRequest = request.getBody().toObject<UserRequest>() ?: return ResponseMessage.error("need login parameter")
        val userDao = ServerRoom.instance.getUserDao()
        val findByUserName = userDao.findByUserName(userRequest.username)
        if (findByUserName==null){
            userDao.insert(User(username = userRequest.username, password = userRequest.password, nickName = "CloudNote User"))
            return ResponseMessage.ok("register success")
        }else{
            return ResponseMessage.error("username is already be use,please change your username")
        }
    }
}