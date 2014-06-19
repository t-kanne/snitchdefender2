package de.mosyapp.snitchdefender;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiverService extends Service {
    TelephonyManager telephonyManager;
    PhoneStateListener listener;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
            	Intent phoneIntent = new Intent ("android.intent.action.MAIN");
                switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    phoneIntent.putExtra("state", "IDLE");
                	getApplicationContext().sendBroadcast(phoneIntent);        
               
                    Log.i("callReceiver", "idle");
                    break;
                    
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    phoneIntent.putExtra("state", "OFFHOOK");
                	getApplicationContext().sendBroadcast(phoneIntent); 
                    
                    Log.i("callReceiver", "offhook");
                    break;
                    
                case TelephonyManager.CALL_STATE_RINGING:
                    phoneIntent.putExtra("state", "RINGING");
                	getApplicationContext().sendBroadcast(phoneIntent); 
                    
                    Log.i("callReceiver", "ringing");
                    break;
                }
                
            }
        };

        // Register the listener with the telephony manager
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("callReceiver", "onDestroy() ausgeführt");
		
		telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
	}



	@Override
	public IBinder onBind(Intent arg0) {
        
		return null;
    }


}