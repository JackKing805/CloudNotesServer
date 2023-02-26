package com.cool.cloudnotesserver.requset.controller

import android.content.Context
import com.cool.cloudnotesserver.db.ServerRoom
import com.cool.cloudnotesserver.db.entity.Note
import com.cool.cloudnotesserver.db.entity.User
import com.cool.cloudnotesserver.db.entity.UserNote
import com.cool.cloudnotesserver.extensions.log
import com.cool.cloudnotesserver.extensions.safeSubList
import com.cool.cloudnotesserver.requset.model.ResponseMessage
import com.jerry.request_base.annotations.Controller
import com.jerry.request_base.bean.RequestMethod
import com.jerry.request_core.anno.PathQuery
import com.jerry.request_core.bean.ParameterBean
import com.jerry.request_core.constants.FileType
import com.jerry.request_shiro.shiro.ShiroUtils
import com.jerry.request_shiro.shiro.anno.ShiroRole
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.core.http.request.model.MultipartFile
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import zlc.season.rxdownload4.download
import zlc.season.rxdownload4.file
import kotlin.concurrent.thread


@Controller("/auth")
class AuthController {
    @Controller("/download", requestMethod = RequestMethod.POST, isRest = true)
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

    @Controller("/note/save", requestMethod = RequestMethod.POST, isRest = true)
    fun onNoteSaveRequest(request: Request,saveNote: SaveNote?):ResponseMessage{
        saveNote?:return ResponseMessage.error("error data")
        val user = ShiroUtils.getAuthInfo(request).authenticationInfo.main as User
        val insert = ServerRoom.instance.getNoteDao().insert(
            Note(
                title = saveNote.title,
                content = saveNote.content,
                lock = saveNote.lock,
                type = saveNote.type
            )
        )
        ServerRoom.instance.getUserNoteDao().insert(UserNote(userId = user.id, noteId = insert))
        return ResponseMessage.ok("save success")
    }

    @Controller("/note/list", isRest = true)
    fun onNotesRequest(request: Request,parameterBean: ParameterBean):ResponseMessage{
        val user = ShiroUtils.getAuthInfo(request).authenticationInfo.main as User
        val start = parameterBean.parameters["start"]?.toInt()?:0
        val size = parameterBean.parameters["size"]?.toInt()?:10
        "onNotesRequest->start:$start,size:$size".log()
        val listByUserId = ServerRoom.instance.getUserNoteDao().listByUserId(user.id).sortedByDescending { it.createTime }
        val safeSubList = listByUserId.safeSubList(start, size)
        val noteDao = ServerRoom.instance.getNoteDao()
        val list = mutableListOf<Note>()
        safeSubList.forEach {
            noteDao.listById(it.noteId)?.let {
                list.add(it)
            }
        }
        return ResponseMessage.ok(list)
    }


    @Controller("file/save", requestMethod = RequestMethod.POST, isRest = true)
    fun onFileUpload(files:List<MultipartFile>):ResponseMessage{
        files.forEach {
            it.save()
        }
        return ResponseMessage.ok("upload success")
    }

    @Controller("file/{name}")
    fun getFile(@PathQuery("name") name:String?="1.mp4"):String{
        "ADas:$name".log()
        return FileType.APP_FILE.content +"save/"+ name
    }
}