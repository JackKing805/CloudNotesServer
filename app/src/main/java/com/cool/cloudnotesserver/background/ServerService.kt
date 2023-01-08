package com.cool.cloudnotesserver.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.UiMessageUtils
import com.cool.cloudnotesserver.R
import com.cool.cloudnotesserver.db.ServerRoom
import com.cool.cloudnotesserver.db.entity.AccessRecord
import com.cool.cloudnotesserver.extensions.log
import com.cool.cloudnotesserver.requset.RequestDelegator
import com.jerry.rt.bean.RtConfig
import com.jerry.rt.core.RtCore
import com.jerry.rt.core.http.Client
import com.jerry.rt.core.http.interfaces.ClientListener
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.core.http.response.impl.StringResponseWriter
import com.jerry.rt.interfaces.RtCoreListener
import java.io.InputStream
import java.lang.Exception
import kotlin.concurrent.thread

class ServerService: Service() {
    companion object{
        fun run(context: Context,run:Boolean){
            ContextCompat.startForegroundService(context,Intent(context,ServerService::class.java).apply {
                putExtra("open",run)
            })
        }
    }

    private var defaultStatus:RtCoreListener.Status? = null
    set(value) {
        value?.let {
            updateNotification(it)
            UiMessageUtils.getInstance().send(1,value)
        }?: kotlin.run {
            UiMessageUtils.getInstance().send(2)
        }

        field = value
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        defaultStatus = RtCoreListener.Status.INIT
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val open = it.getBooleanExtra("open",false)
            if (open){
                startServer()
            }else{
                stopServer()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startServer(){
        thread {
            RtCore.instance.run(RtConfig(), statusListener = object :RtCoreListener{
                override fun onClientIn(client: Client) {
                    "onClientIn".log()
                    client.listen(object :ClientListener{
                        override fun onException(exception: Exception) {
                            exception.printStackTrace()
                        }

                        override fun onInputStreamIn(client: Client, inputStream: InputStream) {
                            
                        }

                        override fun onMessage(
                            client: Client,
                            request: Request,
                            response: Response
                        ) {
                            "onMessage".log()
                            ServerRoom.instance.getAccessRecordDao().insert(AccessRecord(url = request.getPackage().url))
//                            response.getResponseWrite(StringResponseWriter::class).apply {
//                                writeFirstLine(request.getProtocol(),200,"success")
//                                writeHeader("Content-Type","text/html")
//                                writeHeader("Content-Length", "hallo from note server".length)
//                                writeBody("hallo from note server")
//                                endWrite()
//                            }
                            RequestDelegator.dispatcher(this@ServerService,request,response)
                        }

                        override fun onRtHeartbeatIn(client: Client) {
                            "onRtHeartbeatIn".log()
                        }
                    })
                }

                override fun onClientOut(client: Client) {
                    "onClientOut".log()

                }

                override fun onStatusChange(status: RtCoreListener.Status) {
                    "onStatusChange:$status".log()
                    defaultStatus = status

                    if (status==RtCoreListener.Status.STOPPED){
                        defaultStatus = null
                    }
                }
            })
        }
    }

    private fun stopServer(){
        RtCore.instance.stop()
    }

    private fun updateNotification(status:RtCoreListener.Status){
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channel = NotificationChannel("Server","Server",NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableLights(false)
            channel.enableVibration(false)
            channel.setSound(null,null)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this,"Server")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_launcher_foreground))
            .setWhen(System.currentTimeMillis())
            .setContentTitle("Server Status")
            .setContentText(status.name)
            .build()
        startForeground(1,notification)
    }
}