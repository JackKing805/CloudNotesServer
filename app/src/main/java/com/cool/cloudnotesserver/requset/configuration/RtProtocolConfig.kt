package com.cool.cloudnotesserver.requset.configuration

import android.util.Log
import com.jerry.request_base.annotations.Bean
import com.jerry.request_base.annotations.Configuration
import com.jerry.request_core.additation.DefaultRtConfigRegister
import com.jerry.rt.core.http.Client
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.core.http.protocol.RtContentType
import java.util.UUID

@Configuration
class RtProtocolConfig {
    @Bean
    fun rtClient() = object :DefaultRtConfigRegister.RtClient{
        override fun handUrl(): String {
            return "/rt/server"
        }

        override fun onRtIn(client: Client, response: Response) {
            Log.e("WWWWW","onRtIn")
        }

        override fun onRtMessage(request: Request, response: Response) {
            Log.e("WWWWW","onRtMessage:${request.getBody()}")
            response.setContentType(RtContentType.TEXT_PLAIN.content)
            response.write(UUID.randomUUID().toString())
        }

        override fun onRtOut(client: Client, response: Response) {
            Log.e("WWWWW","onRtOut")
        }
    }
}