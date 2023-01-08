package com.cool.cloudnotesserver

import android.app.Application

class ServerApp: Application() {
    companion object{
        lateinit var app:ServerApp
    }

    init {
        app = this
    }

    override fun onCreate() {
        super.onCreate()
    }
}