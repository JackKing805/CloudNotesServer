package com.cool.cloudnotesserver.requset.configuration

import com.cool.cloudnotesserver.db.ServerRoom
import com.jerry.request_base.annotations.Bean
import com.jerry.request_base.annotations.Configuration

@Configuration
class DbConfig {
    @Bean
    fun db() = ServerRoom.instance
}