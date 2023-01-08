package com.cool.cloudnotesserver.requset.controller

import android.content.Context
import android.os.Environment
import com.blankj.utilcode.util.GsonUtils
import com.cool.cloudnotesserver.extensions.log
import com.cool.cloudnotesserver.extensions.parameterToArray
import com.cool.cloudnotesserver.requset.interfaces.Controller
import com.cool.cloudnotesserver.requset.model.ResponseMessage
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.core.http.protocol.RtContentType
import java.io.File

@Controller("/")
class RootController {
    @Controller("/favicon.ico")
    fun onIconRequest(context: Context, request: Request, response: Response) {
        "onIconRequest".log()
        response.writeFile(
            Environment.getExternalStorageDirectory().absolutePath + File.separatorChar + "DCIM" + File.separatorChar + "Camera" + File.separatorChar + "icon.jpg",
            "image/x-icon"
        )
    }

    @Controller("/")
    fun onRootRequest(context: Context, request: Request, response: Response) {
        "onRootRequest".log()
        response.write("Note Server", "text/plain")
    }

    @Controller("/file")
    fun onFileRequest(context: Context, request: Request, response: Response) {
        "onFileRequest".log()
        val requestURI = request.getPackage().getRequestURI()
        val parameterToArray = requestURI.query.parameterToArray()
        parameterToArray["filename"]?.let {
            response.write(ResponseMessage(200,"success",).toJson(), RtContentType.JSON.content)
        }?: kotlin.run {
            response.write(ResponseMessage(-200,"please send filename").toJson(), RtContentType.JSON.content)
        }
    }
}