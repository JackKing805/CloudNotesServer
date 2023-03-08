package com.cool.cloudnotesserver

import android.app.Application
import com.cool.cloudnotesserver.db.ServerRoom
import com.cool.cloudnotesserver.requset.configuration.AuthConfig
import com.cool.cloudnotesserver.requset.configuration.RtProtocolConfig
import com.cool.cloudnotesserver.requset.configuration.WebConfig
import com.cool.cloudnotesserver.requset.controller.AuthController
import com.cool.cloudnotesserver.requset.controller.DealHtmlController
import com.cool.cloudnotesserver.requset.controller.RootController
import com.cool.cloudnotesserver.requset.exception.ServerExceptionHandler
import com.jerry.request_core.Core
import java.io.File

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
            AuthConfig::class.java,
            WebConfig::class.java,
            RootController::class.java,
            AuthController::class.java,
            ServerExceptionHandler::class.java,
            DealHtmlController::class.java,
            RtProtocolConfig::class.java
        ))
        ServerRoom.onCreate()
    }
}