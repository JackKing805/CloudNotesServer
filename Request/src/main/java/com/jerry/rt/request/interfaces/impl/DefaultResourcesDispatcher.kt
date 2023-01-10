package com.jerry.rt.request.interfaces.impl

import android.content.Context
import android.os.Build.VERSION_CODES.P
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.request.R
import com.jerry.rt.request.constants.FileType
import com.jerry.rt.request.extensions.log
import com.jerry.rt.request.interfaces.IResourcesDispatcher

class DefaultResourcesDispatcher: IResourcesDispatcher() {
    //默认图片都读取assets下的图片
    override fun dealResources(
        context: Context,
        request: Request,
        response: Response,
        resourcesName: String
    ): String {
        if (resourcesName=="favicon.ico"){
            return FileType.RAW.content + R.raw.favicon
        }
        return FileType.ASSETS.content + resourcesName
    }

}