package com.cool.cloudnotesserver.requset.configuration

import com.cool.cloudnotesserver.ServerApp
import com.jerry.request_base.annotations.Bean
import com.jerry.request_base.annotations.Configuration
import com.jerry.rt.bean.RtFileConfig
import java.io.File


@Configuration
class WebConfig {

    @Bean
    fun getRtFileConfig() = RtFileConfig(
        tempFileDir = ServerApp.app.filesDir.absolutePath + File.separatorChar + "temp",
        saveFileDir = ServerApp.app.filesDir.absolutePath + File.separatorChar + "save"
    )
}