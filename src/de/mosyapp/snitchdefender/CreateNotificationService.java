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
		Log.i("createNotif", "onCreate() ausgeführt");
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
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		buildNotification(intent.getBooleanExtra("isDefendActive", false));
    	Log.i("createNotif", "onBind() ausgeführt");
        return mBinder;
    }
    private final IBinder mBinder = new LocalBinder();
	

	public void buildNotification(boolean isDefendActive) {
		Log.i("createNotif", "isDefendActive: " + isDefendActive);
		Notification n;
		NotificationCompat.Builder builder;
		
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		builder = new NotificationCompat.Builder(this);

		builder.setContentIntent(pIntent)
			.setContentTitle("Snitch Defender")
			.setSmallIcon(R.drawable.sd_icon)
			.setOnlyAlertOnce(true)
			.setContentIntent(pIntent);
		
		if (isDefendActive) {
			builder.setContentText("Diebstahlschutz ist aktiv");
		} else {
			builder.setContentText("Diebstahlschutz ist nicht aktiv");
		}
		
		n = builder.build();
		nm.notify(notifId, n);
	}
}
