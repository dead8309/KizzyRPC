package kizzy.rpc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.my.kizzyrpc.KizzyRPCservice;

public class MyService extends Service {
    String CHANNEL_ID = "Discord Kizzy RPC";
    String token;
    Context context;
    KizzyRPCservice kizzyRPCservice;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        token = MainActivity.token;

        kizzyRPCservice = new KizzyRPCservice(token);
        kizzyRPCservice.setApplicationId("962990036020756480")
                .setName("hi")
                .setDetails("details")
                .setLargeImage("attachments/973256105515974676/983674644823412798/unknown.png")
                .setSmallImage("attachments/973256105515974676/983674644823412798/unknown.png")
                .setState("state")
                .setType(0)
                .setStartTimestamps(System.currentTimeMillis())
                .setButton1("button1","https://youtu.be/1yVm_M1sKBE")
                .setButton2("button2","https://youtu.be/1yVm_M1sKBE")
                .setStatus("online")
                .build();
        notification();
        
    }

    @Override
    public void onDestroy() {
        kizzyRPCservice.closeRPC();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void notification (){
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "Background Service", NotificationManager.IMPORTANCE_LOW));
        Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentText("Rpc Running");
        builder.setUsesChronometer(true);
        startForeground(11234,builder.build());
    }
}