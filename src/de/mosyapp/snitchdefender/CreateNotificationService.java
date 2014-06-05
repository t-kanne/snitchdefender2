package de.mosyapp.snitchdefender;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class CreateNotificationService extends Service {
	private NotificationManager nm;
	private int notifId = 1;
	
	public class LocalBinder extends Binder {
		CreateNotificationService getService() {
			return CreateNotificationService.this;
		}
	}

	@Override
	public void onCreate() {
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		buildNotification();	
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("CreateNotificationService", "start id: " + startId + ": " + intent);
		return START_STICKY;
	}


	@Override
	public void onDestroy() {
		nm.cancel(notifId);
		Log.i("CreateNotificationService", "Benachrichtigung wurde gestoppt");
	}
	
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    private final IBinder mBinder = new LocalBinder();
	
	
	public void buildNotification() {
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

		builder.setContentIntent(pIntent)
			.setContentTitle("Snitch Defender")
		    .setContentText("Zum Konfigurieren berühren").setSmallIcon(R.drawable.sd_icon)
		    .setContentIntent(pIntent);

		Notification n = builder.build();


		nm.notify(notifId, n);

	}
	
}
