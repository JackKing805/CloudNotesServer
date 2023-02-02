package com.cool.cloudnotesserver.requset.exception

import com.jerry.request_core.anno.ExceptionHandler
import com.jerry.request_core.anno.ExceptionRule
import com.jerry.request_shiro.shiro.exception.ShiroException

@ExceptionRule
class ServerExceptionHandler {

    @ExceptionHandler(exceptionClasses = ShiroException::class)
    fun onShiroException(shiroException: ShiroException):String{
        return "not auth"
    }
}