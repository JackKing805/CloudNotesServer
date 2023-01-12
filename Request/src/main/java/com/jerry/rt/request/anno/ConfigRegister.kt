package com.jerry.rt.request.anno

import java.lang.annotation.Inherited

/**
 * 配置注册注解，需要搭配IConfig同时使用
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class ConfigRegister(
  val priority:Int = 0
)
