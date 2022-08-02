package kizzy.rpc

import android.app.Notification

import com.my.kizzyrpc.KizzyRPCservice
import android.content.Intent
import android.os.IBinder
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.Service
import android.content.Context


class MyService : Service() {
    private var CHANNEL = "Discord RPC"
    private var token: String? = null
    private var context: Context? = null
    private var rpc: KizzyRPCservice? = null
    override fun onCreate() {
        super.onCreate()
        context = this
        token = MainActivity.token
        rpc = KizzyRPCservice(token!!)
        rpc!!.setApplicationId("962990036020756480")
                .setName("hi")
                .setDetails("details")
                .setLargeImage("attachments/973256105515974676/983674644823412798/unknown.png")
                .setSmallImage("attachments/973256105515974676/983674644823412798/unknown.png")
                .setState("state")
                .setType(0)
                .setStartTimestamps(System.currentTimeMillis())
                .setButton1("button1", "https://youtu.be/1yVm_M1sKBE")
                .setButton2("button2", "https://youtu.be/1yVm_M1sKBE")
                .setStatus("online")
                .build()
        notification()
    }

    override fun onDestroy() {
        rpc!!.closeRPC()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun notification() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(NotificationChannel(CHANNEL, "Background Service", NotificationManager.IMPORTANCE_LOW))
        val builder = Notification.Builder(context, CHANNEL)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentText("Rpc Running")
        builder.setUsesChronometer(true)
        startForeground(11234, builder.build())
    }
}