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
    fun onRegisterRequest(context: Context, request: Request, response: Response,userRequest: UserRequest?): ResponseMessage {
        userRequest ?: return ResponseMessage.error("need register parameter")
        if (userRequest.username.isEmpty()){
            return ResponseMessage.error("username can't be empty")
        }

        if (userRequest.password.isEmpty()){
            return ResponseMessage.error("password can't be empty")
        }

        if (userRequest.username.length<5){
            return ResponseMessage.error("username length must more than 5")
        }

        if(!validatePassword(userRequest.password)){
            return ResponseMessage.error("password so easy")
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


    private fun validatePassword(password: String): Boolean {
        val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$")
        return passwordRegex.matches(password)
    }
}