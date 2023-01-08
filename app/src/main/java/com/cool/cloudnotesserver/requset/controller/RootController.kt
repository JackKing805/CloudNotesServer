package com.cool.cloudnotesserver.requset.controller

import android.content.Context
import android.os.Environment
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.cool.cloudnotesserver.db.ServerRoom
import com.cool.cloudnotesserver.db.entity.User
import com.cool.cloudnotesserver.extensions.log
import com.cool.cloudnotesserver.extensions.parameterToArray
import com.cool.cloudnotesserver.extensions.toKotlinString
import com.cool.cloudnotesserver.extensions.toObject
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
    fun onIconRequest(context: Context, request: Request, response: Response) {
        "onIconRequest".log()
        response.writeFile(
            Environment.getExternalStorageDirectory().absolutePath + File.separatorChar + "DCIM" + File.separatorChar + "Camera" + File.separatorChar + "icon.jpg",
            "image/x-icon"
        )
    }

    @Controller("/")
    fun onRootRequest(context: Context, request: Request, response: Response) {
        "onRootRequest".log()
        response.write("Note Server", "text/plain")
    }

    private data class UserRequest(
        val username:String,
        val password:String
    )

    @Controller("/login", requestMethod = RequestMethod.POST)
    fun onLoginRequest(context: Context, request: Request, response: Response){
        "onLoginRequest".log()

        val userRequest = request.getBody().toObject<UserRequest>()
        if (userRequest==null){
            response.write(ResponseMessage.error("need login parameter"),RtContentType.JSON.content)
            return
        }
        val userDao = ServerRoom.instance.getUserDao()
        val findByUserName = userDao.findByUserName(userRequest.username)
        if (findByUserName==null){
            response.write(ResponseMessage.error("username not exits"),RtContentType.JSON.content)
        }else{
            if (findByUserName.password==userRequest.password){
                val cacheToken = SPUtils.getInstance().getString(findByUserName.username, "")
                val token = cacheToken.ifEmpty {
                    UUID.randomUUID().toString()
                }
                SPUtils.getInstance().put(findByUserName.username,token)
                response.write(ResponseMessage.ok("login success",token),RtContentType.JSON.content)
            }else{
                response.write(ResponseMessage.error("username or password not correct"),RtContentType.JSON.content)
            }
        }
    }

    @Controller("/register", requestMethod = RequestMethod.POST)
    fun onRegisterRequest(context: Context, request: Request, response: Response){
        "onRegisterRequest".log()

        val userRequest = request.getBody().toObject<UserRequest>()
        if (userRequest==null){
            response.write(ResponseMessage.error("need login parameter"),RtContentType.JSON.content)
            return
        }
        val userDao = ServerRoom.instance.getUserDao()
        val findByUserName = userDao.findByUserName(userRequest.username)
        if (findByUserName==null){
            userDao.insert(User(username = userRequest.username, password = userRequest.password, nickName = "CloudNote User"))
            response.write(ResponseMessage.ok("register success"),RtContentType.JSON.content)
        }else{
            response.write(ResponseMessage.error("username is already be use,please change your username"),RtContentType.JSON.content)
        }
    }
}