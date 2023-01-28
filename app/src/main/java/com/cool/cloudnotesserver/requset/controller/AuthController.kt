package com.cool.cloudnotesserver.requset.controller

import android.content.Context
import com.cool.cloudnotesserver.db.ServerRoom
import com.cool.cloudnotesserver.db.entity.Note
import com.cool.cloudnotesserver.extensions.log
import com.cool.cloudnotesserver.extensions.safeSubList
import com.cool.cloudnotesserver.requset.model.ResponseMessage
import com.jerry.request_base.annotations.Controller
import com.jerry.request_base.bean.RequestMethod
import com.jerry.request_core.bean.ParameterBean
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import zlc.season.rxdownload4.download
import zlc.season.rxdownload4.file
import kotlin.concurrent.thread

@Controller("/auth", isRest = true)
class AuthController {
    @Controller("/download", requestMethod = RequestMethod.POST)
    fun onFileRequest(context: Context, request: Request, response: Response, parameterBean: ParameterBean) :ResponseMessage{
        val get = parameterBean.parameters.get("path") ?: return ResponseMessage.error("no valid download path")
       thread {
           val disposable = get.download()
               .observeOn(AndroidSchedulers.mainThread())
               .subscribeBy(
                   onNext = { progress ->
                       //下载进度
                       "downloadP:$progress".log("AAA")
                   },
                   onComplete = {
                       //下载完成
                       "downloadSuccess:${get.file().absolutePath}".log("AAA")
                   },
                   onError = {
                       //下载失败
                       "downloadError:$it".log("AAA")
                   }
               )
       }
        return ResponseMessage.ok("start download")
    }

    data class SaveNote(
        val title:String,
        val content:String,
        val lock:Boolean = false,
        val type:String
    )

    @Controller("/note/save", requestMethod = RequestMethod.POST)
    fun onNoteSaveRequest(request: Request,saveNote: SaveNote?):ResponseMessage{
        saveNote?:return ResponseMessage.error("error data")
        ServerRoom.instance.getNoteDao().insert(Note(
            title =saveNote.title,
            content = saveNote.content,
            lock = saveNote.lock,
            type = saveNote.type
        ))
        return ResponseMessage.ok("save success")
    }

    @Controller("/note/list")
    fun onNotesRequest(request: Request,parameterBean: ParameterBean):ResponseMessage{
        val start = parameterBean.parameters["start"]?.toInt()?:0
        val size = parameterBean.parameters["size"]?.toInt()?:10
        "onNotesRequest->start:$start,size:$size".log()
        val list = ServerRoom.instance.getNoteDao().list().sortedBy { -it.lastModifyTime }
        val subList = list.safeSubList(start, size)
        return ResponseMessage.ok(subList)
    }
}