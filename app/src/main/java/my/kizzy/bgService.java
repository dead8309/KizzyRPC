package my.kizzy;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.os.IBinder;
import android.content.Intent;

import com.my.kizzy.KizzyRPCservice;



public class bgService extends Service {
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
		kizzyRPCservice.setName("hi")
						.setDetails("details")
								.setLargeImage("attachments/948828217312178227/948840504542498826/Kizzy.png")
										.setSmallImage("attachments/948828217312178227/948840504542498826/Kizzy.png")
												.setState("state")
														.setType(0)
																.setStartTimestamps(System.currentTimeMillis())
																		/*.setStopTimestamps(System.currentTimeMillis()+15000)*/
																				.setStatus("dnd")
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
		builder.setSmallIcon(R.mipmap.noti);
		builder.setContentText("Rpc Running");
		builder.setUsesChronometer(true);
		startForeground(11234,builder.build());
	}
}
