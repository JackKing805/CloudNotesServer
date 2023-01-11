package com.jerry.rt.request.configuration

import android.content.Context
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.request.anno.Configuration
import com.jerry.rt.request.extensions.log
import com.jerry.rt.request.interfaces.IAuthDispatcher

@Configuration
class DefaultAuthDispatcher: IAuthDispatcher() {
    override fun onAuth(context: Context, request: Request, response: Response): Boolean {
        "auth:${request.getPackage().getRequestURI().path}".log()
        return true
    }

}