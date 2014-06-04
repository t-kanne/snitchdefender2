package de.mosyapp.snitchdefender;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.view.View;


public class CreateNotificationActivity extends Activity {
  
	private String isActivated;
	Context context;
	
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_create_notification);
  }
  
  public void isActive(boolean isActive){
	  if (isActive) {
		  isActivated = "aktiv";
	  } else {
		  isActivated = "nicht aktiv";
	  }
  }
  

public void createNotification(View view) {
    // Prepare intent which is triggered if the
    // notification is selected
    Intent intent = new Intent(this, MainActivity.class);
    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

    // Build notification
    // Actions are just fake
    Builder noti = new NotificationCompat.Builder(context);
        noti.setContentTitle("Snitch Defender")
        .setContentText("Diebstahlschutz ist " + isActivated).setSmallIcon(R.drawable.sd_icon)
        .setContentIntent(pIntent);

   // NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    // hide the notification after its selected
    
    //Notification.FLAG_AUTO_CANCEL;

    //notificationManager.notify(0, noti);

  }
} 