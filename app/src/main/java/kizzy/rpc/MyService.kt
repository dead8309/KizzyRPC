package kizzy.rpc

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.my.kizzyrpc.KizzyRPC
import com.my.kizzyrpc.model.Activity
import com.my.kizzyrpc.model.Assets
import com.my.kizzyrpc.model.Metadata
import com.my.kizzyrpc.model.Timestamps

class MyService : Service() {

    companion object {
        const val CHANNEL = "Discord RPC"
        const val START_ACTIVITY_ACTION = "START_ACTIVITY_ACTION"
        var token: String? = null
    }

    private var rpc: KizzyRPC? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        token = intent?.getStringExtra("TOKEN")
        rpc = token?.let { KizzyRPC(it) }
        if (intent?.action.equals(START_ACTIVITY_ACTION)) {
            rpc?.setActivity(
                activity = Activity(
                    applicationId = "962990036020756480",
                    name = "hi",
                    details = "details",
                    state = "state",
                    type = 0,
                    timestamps = Timestamps(
                        start = System.currentTimeMillis(),
                        end = System.currentTimeMillis() + 500000
                    ),
                    assets = Assets(
                        largeImage = "mp:attachments/973256105515974676/983674644823412798/unknown.png",
                        smallImage = "mp:attachments/973256105515974676/983674644823412798/unknown.png",
                        largeText = "large-image-text",
                        smallText = "small-image-text",
                    ),
                    buttons = listOf("Button 1", "Button 2"),
                    metadata = Metadata(
                        listOf(
                            "https://youtu.be/1yVm_M1sKBE",
                            "https://youtu.be/1yVm_M1sKBE",
                        )
                    )
                ),
                status = "online",
                since = System.currentTimeMillis()
            )

        }
        notification()
        return super.onStartCommand(intent, flags, startId)
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
        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL,
                "Background Service",
                NotificationManager.IMPORTANCE_LOW
            )
        )
        val builder = Notification.Builder(this, CHANNEL)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentText("Rpc Running")
        builder.setUsesChronometer(true)
        startForeground(11234, builder.build())
    }
}