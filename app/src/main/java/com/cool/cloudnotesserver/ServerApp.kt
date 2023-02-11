package com.cool.cloudnotesserver

import android.app.Application
import com.cool.cloudnotesserver.db.ServerRoom
import com.cool.cloudnotesserver.requset.configuration.AuthConfig
import com.cool.cloudnotesserver.requset.controller.AuthController
import com.cool.cloudnotesserver.requset.controller.RootController
import com.cool.cloudnotesserver.requset.exception.ServerExceptionHandler
import com.jerry.request_core.Core
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            AuthConfig::class.java,
            ServerExceptionHandler::class.java
        ))

        ServerRoom.onCreate()
    }
}