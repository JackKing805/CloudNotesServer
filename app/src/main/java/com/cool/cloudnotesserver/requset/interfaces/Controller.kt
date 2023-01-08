package com.cool.cloudnotesserver.requset.interfaces

@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Controller(
    val value:String
)
