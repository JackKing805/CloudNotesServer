package com.jerry.rt.additation.configuration

import android.content.Context
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.request.R
import com.jerry.rt.request.anno.Configuration
import com.jerry.rt.request.constants.FileType
import com.jerry.rt.additation.interfaces.IResourcesDispatcher

@Configuration
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