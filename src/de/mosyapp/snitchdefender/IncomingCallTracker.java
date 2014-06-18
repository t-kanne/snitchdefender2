package de.mosyapp.snitchdefender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class IncomingCallTracker extends BroadcastReceiver {
	
    @Override
    public void onReceive(Context context, Intent intent) {
   	
        Bundle bundle = intent.getExtras();
        String state = String.valueOf(bundle.get("state"));
        Intent phoneIntent = new Intent (context.getApplicationContext(), MainActivity.class);
        
        if (state.equalsIgnoreCase("IDLE")){
        	Log.i("incomingcalls", "state = idle"); 
        	phoneIntent.putExtra("state", "IDLE");
        	phoneIntent.setClass(context, MainActivity.class);
        	phoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	context.startActivity(phoneIntent);
        }
         
        if (state.equalsIgnoreCase("RINGING")){
        	Log.i("incomingcalls", "state = ringing"); 
        	phoneIntent.putExtra("state", "RINGING");
        	phoneIntent.setClass(context, MainActivity.class);
        	phoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	context.startActivity(phoneIntent);	
        }
        
        if (state.equalsIgnoreCase("OFFHOOK")){
        	Log.i("incomingcalls", "state = offhook");
        	phoneIntent.putExtra("state", "OFFHOOK");
        	phoneIntent.setClass(context, MainActivity.class);
        	phoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	context.startActivity(phoneIntent);		
        }
       
    }

}