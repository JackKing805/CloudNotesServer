package com.cool.cloudnotesserver

import android.app.Application
import com.cool.cloudnotesserver.requset.controller.AuthController
import com.jerry.rt.request.RequestUtils
import com.jerry.rt.request.config.Config
import com.cool.cloudnotesserver.requset.controller.RootController

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
            AuthController::class.java
        ))
    }
}