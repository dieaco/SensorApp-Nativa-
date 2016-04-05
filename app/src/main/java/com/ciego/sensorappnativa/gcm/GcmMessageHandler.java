package com.ciego.sensorappnativa.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.ciego.sensorappnativa.DashbordActivity;
import com.ciego.sensorappnativa.SplashScreenActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ciego.sensorappnativa.R;

public class GcmMessageHandler extends IntentService{

	//Atributos para Notifications en barra de estado
    String message;
    String title;
    Uri alarmSound;
    private final int NOTIFICATION_ID = 12345;
    //GCM
    private Handler handler;
    //Noitificaciones
    private NotificationManager mNotificationManager;
    //Variables para controlar grupos
    private static int number = 0;
    private static final String GROUP_NOTIFICATIONS = "group_notifications";
	
	public GcmMessageHandler() {
		super("GcmMessageHandler");
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		handler = new Handler();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
        number++;
        Log.d("Number of notification: ", " "+number);

		Bundle extras = intent.getExtras();
		
		GoogleCloudMessaging gcm = new GoogleCloudMessaging().getInstance(this);
		
		String messageType =  gcm.getMessageType(intent);
		
		message = extras.getString("message");
		title = extras.getString("title");
		alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent notificationIntent = new Intent(getApplicationContext(), DashbordActivity.class);
        notificationIntent.putExtra("mensaje", message);
        notificationIntent.putExtra("titulo", title);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		
		mNotificationManager = (NotificationManager)this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
				notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = new BitmapFactory().decodeResource(getResources(), R.drawable.logosensor50);
		
		NotificationCompat.Builder mBuilder = 
				new NotificationCompat.Builder(getApplicationContext())	
				.setSmallIcon(R.drawable.logosensor50)
                .setLargeIcon(largeIcon)
				.setContentTitle(title)
				.setContentText(message)
                .setAutoCancel(true)
				.setSound(alarmSound)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
				.setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message)
                        .setSummaryText(number+" mensajes nuevo"))
                .setGroup(GROUP_NOTIFICATIONS)
                .setGroupSummary(true);
		
		mBuilder.setContentIntent(pendingIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		
		//showToast();
		
		Log.i("GCM", "Received: ("+messageType+") "+extras.getString("title"));
		
		//Para indicar que una vez sea posible apague el servicio
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	
	public void showToast(){
		handler.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
			
		});
	}

}
