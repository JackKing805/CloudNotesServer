package com.jerry.rt.request.anno

import java.lang.annotation.Inherited


@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class Controller(
    val value:String="",
    val requestMethod: RequestMethod = RequestMethod.GET,
    val isRest:Boolean = false
)
