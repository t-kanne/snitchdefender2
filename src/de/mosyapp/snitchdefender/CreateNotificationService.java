package de.mosyapp.snitchdefender;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class CreateNotificationService extends IntentService {
  
	public CreateNotificationService() {
		super("CreateNotificationService");
	}
  
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		intent = new Intent(this, MainActivity.class);
	    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	
	    NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
	    
	    Resources res = this.getResources();
	    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
	    
	    builder.setContentIntent(pIntent)
	    	.setContentTitle("Snitch Defender")
	        .setContentText("Zum Konfigurieren berühren").setSmallIcon(R.drawable.sd_icon)
	        .setContentIntent(pIntent);
	
	    Notification n = builder.build();
	    
	    nm.notify(1, n);
		
		Log.i("notifService", "Test");
	
	}
	

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
	}
/*
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	    NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
	    nm.cancel(1);
		
	}
*/

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
	    nm.cancel(1);
		return super.onUnbind(intent);
	}	
	


} 