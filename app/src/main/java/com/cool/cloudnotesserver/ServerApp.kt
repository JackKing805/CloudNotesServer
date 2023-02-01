package com.cool.cloudnotesserver

import android.app.Application
import com.cool.cloudnotesserver.requset.configuration.AuthConfig
import com.cool.cloudnotesserver.requset.controller.AuthController
import com.cool.cloudnotesserver.requset.controller.RootController
import com.jerry.request_core.Core

class ServerApp: Application() {
    companion object{
        lateinit var app:ServerApp
    }

    init {
        app = this
    }

    override fun onCreate() {
        super.onCreate()

        Core.init(this, mutableListOf(
            RootController::class.java,
            AuthController::class.java,
            AuthConfig::class.java
        ))
    }
}