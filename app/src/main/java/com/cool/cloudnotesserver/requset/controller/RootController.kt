package com.cool.cloudnotesserver.requset.controller

import android.content.Context
import com.cool.cloudnotesserver.db.ServerRoom
import com.cool.cloudnotesserver.db.entity.User
import com.cool.cloudnotesserver.extensions.log
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.cool.cloudnotesserver.requset.model.ResponseMessage
import com.jerry.request_base.annotations.Controller
import com.jerry.request_base.annotations.RequestMethod
import com.jerry.request_core.bean.ParameterBean
import com.jerry.request_core.constants.FileType
import com.jerry.request_core.extensions.toObject
import java.io.File
import java.util.UUID

@Controller("/")
class RootController {
    @Controller("/")
    fun onRootRequest(context: Context, request: Request, response: Response):String {
        "onRootRequest:${Thread.currentThread()}".log()
        return FileType.ASSETS.content + "index.html"
    }

    data class UserRequest(
        val username:String,
        val password:String
    )

    @Controller(value = "/login",requestMethod = RequestMethod.POST, isRest = true)
    fun onLoginRequest(context: Context, request: Request, response: Response, userRequest: UserRequest?): ResponseMessage {
        "onLoginRequest:${Thread.currentThread()}".log()

        if (userRequest==null){
            return ResponseMessage.error("need login parameter")
        }
        val userDao = ServerRoom.instance.getUserDao()
        val findByUserName = userDao.findByUserName(userRequest.username)
        if (findByUserName==null){
            return ResponseMessage.error("username not exits")
        }else{
            if (findByUserName.password==userRequest.password){
                val cacheToken = findByUserName.token
                val token = cacheToken.ifEmpty {
                    UUID.randomUUID().toString()
                }
                userDao.update(findByUserName.copy(token = token))
                return ResponseMessage.ok("login success",token)
            }else{
                return ResponseMessage.error("username or password not correct")
            }
        }
    }

    @Controller(value = "/login/verify_token",requestMethod = RequestMethod.GET, isRest = true)
    fun onLoginTokenRequest(context: Context, request: Request, response: Response): ResponseMessage {
        "onLoginTokenRequest:${Thread.currentThread()}".log()
        val s = request.getPackage().getHeaderValue("Token","")
        if (s.isEmpty()){
            return ResponseMessage.error("verify failure")
        }else{
            val userDao = ServerRoom.instance.getUserDao()
            val findByUserToken = userDao.findByUserToken(s)
            if (findByUserToken==null){
                return ResponseMessage.error("verify failure")
            }
            return ResponseMessage.ok("verify success")
        }
    }

    @Controller(value = "/register",requestMethod = RequestMethod.POST, isRest = true)
    fun onRegisterRequest(context: Context, request: Request, response: Response): ResponseMessage {
        "onRegisterRequest".log()

        val userRequest = request.getBody().toObject<UserRequest>() ?: return ResponseMessage.error("need register parameter")
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