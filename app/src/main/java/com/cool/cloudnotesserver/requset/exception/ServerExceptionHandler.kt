package com.cool.cloudnotesserver.requset.exception

import com.cool.cloudnotesserver.requset.model.ResponseMessage
import com.jerry.request_core.anno.ExceptionHandler
import com.jerry.request_core.anno.ExceptionRule
import com.jerry.request_shiro.shiro.exception.ShiroAuthException
import com.jerry.request_shiro.shiro.exception.ShiroVerifyException

@ExceptionRule
class ServerExceptionHandler {

    @ExceptionHandler(exceptionClasses = ShiroAuthException::class)
    fun onShiroException(shiroException: ShiroAuthException):ResponseMessage{
        return ResponseMessage.error("not auth")
    }


    @ExceptionHandler(exceptionClasses = ShiroVerifyException::class)
    fun onShiroVerifyException(shiroVerifyException: ShiroVerifyException):ResponseMessage{
        return ResponseMessage.error("verify error")
    }
}