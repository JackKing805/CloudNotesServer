package com.jerry.rt.request.anno

import java.lang.annotation.Inherited

/**
 * 配置注解
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class Configuration
