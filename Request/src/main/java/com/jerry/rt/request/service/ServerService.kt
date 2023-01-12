package com.jerry.rt.request.service

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
import com.jerry.rt.bean.RtConfig
import com.jerry.rt.core.RtCore
import com.jerry.rt.core.http.Client
import com.jerry.rt.core.http.interfaces.ClientListener
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import com.jerry.rt.interfaces.RtCoreListener
import com.jerry.rt.request.RequestUtils
import com.jerry.rt.request.constants.Status
import com.jerry.rt.request.delegator.RequestDelegator
import com.jerry.rt.request.extensions.log
import java.io.InputStream
import java.lang.Exception
import kotlin.concurrent.thread

internal class ServerService: Service() {
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
            RequestUtils.getIRequestListener()?.onStatusChange(Status.rtStatusToStats(it))
        }?:run {
            RequestUtils.getIRequestListener()?.onStatusChange(Status.rtStatusToStats(null))
        }
        field = value
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        defaultStatus = null
        updateNotification(RtCoreListener.Status.STOPPED)
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
        RtCore.instance.run(RtConfig(), statusListener = object :RtCoreListener{
            override fun onClientIn(client: Client) {
                "onClientIn".log()
                client.listen(object :ClientListener{
                    override fun onException(exception: Exception) {
                        exception.printStackTrace()
                    }

                    override suspend fun onInputStreamIn(client: Client, inputStream: InputStream) {

                    }

                    override suspend fun onMessage(
                        client: Client,
                        request: Request,
                        response: Response
                    ) {
                        "onMessage".log()
                        RequestDelegator.dispatcher(this@ServerService,request,response)
                    }

                    override suspend fun onRtHeartbeatIn(client: Client) {
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

                if (defaultStatus==RtCoreListener.Status.STOPPED){
                    defaultStatus = null
                }
            }
        })
    }

    private fun stopServer(){
        RtCore.instance.stop()
    }

    private fun updateNotification(status:RtCoreListener.Status){
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel("Server","Server",NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableLights(false)
        channel.enableVibration(false)
        channel.setSound(null,null)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        manager.createNotificationChannel(channel)

        val config = RequestUtils.getConfig()
        val notification = NotificationCompat.Builder(this,"Server")
            .setSmallIcon(config.appIcon)
            .setLargeIcon(BitmapFactory.decodeResource(resources,config.appIcon))
            .setWhen(System.currentTimeMillis())
            .setContentTitle("Server Status")
            .setContentText(status.name)
            .build()
        startForeground(1,notification)
    }
}