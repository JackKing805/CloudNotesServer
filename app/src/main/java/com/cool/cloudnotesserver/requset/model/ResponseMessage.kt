package com.cool.cloudnotesserver.requset.model

import com.blankj.utilcode.util.GsonUtils

data class ResponseMessage(
    val code: Int,
    val message: String,
    val body: Any? = null
) {
    fun toJson(): String {
        return GsonUtils.toJson(this)
    }

    companion object {
        fun create(code: Int, message: String, data: Any? = null): ResponseMessage {
            return ResponseMessage(code, message, data)
        }

        fun error() = create(-200, "error")
        fun error(msg: String, data: Any? = null) = create(-200, msg, data)
        fun error(data: Any) = create(-200, "error", data)
        fun ok(msg: String, data: Any? = null) = create(200, msg, data)
        fun ok(data: Any) = create(200, "success", data)
    }
}