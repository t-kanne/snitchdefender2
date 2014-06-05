package de.mosyapp.snitchdefender;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.LinearLayout;


public class CreateNotificationActivity extends Activity {
  
	private String isActivated;
	Context context;
	
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_create_notification);

    
    Intent intent = new Intent(this, MainActivity.class);
    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

    NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    
    Resources res = this.getResources();
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    
    builder.setContentIntent(pIntent)
    	.setContentTitle("Snitch Defender")
        .setContentText("Diebstahlschutz ist " + isActivated).setSmallIcon(R.drawable.sd_icon)
        .setContentIntent(pIntent);

    Notification n = builder.build();
    
    nm.notify(1, n);
  
  }
  

/*
public void createNotification(View view) {
    // Prepare intent which is triggered if the
    // notification is selected
    Intent intent = new Intent(this, MainActivity.class);
    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

    NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    
    Resources res = this.getResources();
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    
    builder.setContentIntent(pIntent)
    	.setContentTitle("Snitch Defender")
        .setContentText("Diebstahlschutz ist " + isActivated).setSmallIcon(R.drawable.sd_icon)
        .setContentIntent(pIntent);

    Notification n = builder.build();
    
    nm.notify(1, n);

  }
*/
} 