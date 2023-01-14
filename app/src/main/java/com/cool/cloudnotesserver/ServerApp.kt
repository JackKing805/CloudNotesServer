package com.cool.cloudnotesserver

import android.app.Application
import com.cool.cloudnotesserver.requset.configuration.AuthConfig
import com.cool.cloudnotesserver.requset.controller.AuthController
import com.cool.cloudnotesserver.requset.controller.RootController
import com.jerry.request_core.RequestUtils
import com.jerry.request_core.config.Config

class ServerApp: Application() {
    companion object{
        lateinit var app:ServerApp
    }

    init {
        app = this
    }

    override fun onCreate() {
        super.onCreate()

        RequestUtils.init(this, Config(R.drawable.ic_launcher_foreground), mutableListOf(
            RootController::class.java,
            AuthController::class.java,
            AuthConfig::class.java
        ))
    }
}